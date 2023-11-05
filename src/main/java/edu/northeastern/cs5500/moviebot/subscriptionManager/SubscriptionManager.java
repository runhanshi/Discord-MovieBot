package edu.northeastern.cs5500.moviebot.subscriptionManager;

import static com.mongodb.client.model.Filters.eq;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.InteractPage;
import com.github.ygimenez.model.Page;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.App;
import edu.northeastern.cs5500.moviebot.messageBuilder.MovieMessageBuilder;
import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.tools.ApiUrl;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;

public class SubscriptionManager {

    static final String QUERY_METHOD = "discover";
    static final String QUERY_CATEGORY = "movie";
    static final String QUERY_PARAM_PAGE_KEY = "page";
    static final int QUERY_PARAM_PAGE_VALUE = 1;
    static final String QUERY_PARAM_LANGUAGE_KEY = "language";
    static final String QUERY_PARAM_LANGUAGE_VALUE = "en-US";
    static final String QUERY_PARAM_REGION_KEY = "region";
    static final String QUERY_PARAM_WATCH_REGION_KEY = "watch_region";
    static final String QUERY_PARAM_REGION_VALUE = "US";
    static final String QUERY_PARAM_PRIMARY_RELEASE_DATE_START_KEY = "primary_release_date.gte";
    static final String QUERY_PARAM_PRIMARY_RELEASE_DATE_END_KEY = "primary_release_date.lte";
    static final String QUERY_PARAM_WITH_RELEASE_TYPE_KEY = "with_release_type";
    static final int QUERY_PARAM_WITH_RELEASE_TYPE_VALUE = 2;
    static final String QUERY_PARAM_INCLUDE_ADULT_KEY = "include_adult";
    static final boolean QUERY_PARAM_INCLUDE_ADULT_VALUE = false;
    static final String QUERY_PARAM_PRIMARY_RELEASE_DATE_FORMAT = "yyyy-MM-dd";
    static final int MAX_NUM_MOVIE_RESULTS = 10;
    static final String USER_COLLECTION_NAME = "User";
    static final String USER_FIELD_NAME_SUBSCRIBE_STATUS = "subscribed";
    static final String MSG_MOVIE_RELEASE_NOT_FOUND =
            "Oops... Looks like there is no movie release today.";
    final ScheduledExecutorService SUBSCRIPTION_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor();

    /**
     * Generate the movie release date by using the current date in UTC.
     *
     * @return the formatted release date.
     */
    String generateMovieReleaseDate() {
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(QUERY_PARAM_PRIMARY_RELEASE_DATE_FORMAT);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return dateFormatter.format(now);
    }

    /**
     * Construct a request url that fetches today's movie release information from TMDb.
     *
     * @return the request url that fetches today's movie releases information from TMDb.
     */
    ApiUrl constructApiRequestUrl() {
        String today = this.generateMovieReleaseDate();
        return new ApiUrl(QUERY_METHOD, QUERY_CATEGORY) {
            {
                addParam(QUERY_PARAM_PAGE_KEY, QUERY_PARAM_PAGE_VALUE);
                addParam(QUERY_PARAM_LANGUAGE_KEY, QUERY_PARAM_LANGUAGE_VALUE);
                addParam(QUERY_PARAM_REGION_KEY, QUERY_PARAM_REGION_VALUE);
                addParam(QUERY_PARAM_WATCH_REGION_KEY, QUERY_PARAM_REGION_VALUE);
                addParam(QUERY_PARAM_PRIMARY_RELEASE_DATE_START_KEY, today);
                addParam(QUERY_PARAM_PRIMARY_RELEASE_DATE_END_KEY, today);
                addParam(QUERY_PARAM_WITH_RELEASE_TYPE_KEY, QUERY_PARAM_WITH_RELEASE_TYPE_VALUE);
                addParam(QUERY_PARAM_INCLUDE_ADULT_KEY, QUERY_PARAM_INCLUDE_ADULT_VALUE);
            }
        };
    }

    /**
     * Fetch today's movie release information from TMDb and narrow it down to top ten.
     *
     * @return today's top-ten movie release information from TMDb.
     */
    List<MovieDb> fetchTopTenMovieReleaseInfo() {
        TmdbApi api = new TmdbApi(App.getAPIKey());
        ApiUrl ApiUrl = this.constructApiRequestUrl();
        List<MovieDb> allMovies =
                api.getDiscover().mapJsonResult(ApiUrl, MovieResultsPage.class).getResults();
        List<MovieDb> topTenMovies = new ArrayList<>();
        for (int i = 0; i < MAX_NUM_MOVIE_RESULTS && i < allMovies.size(); i++) {
            topTenMovies.add(allMovies.get(i));
        }
        return topTenMovies;
    }

    /**
     * Extract the users whose subscribe status is true from the MongoDb's user collection.
     *
     * @return the discord user ids of extracted users as a list.
     */
    List<Long> getSubscribedUserIds() {
        MongoCollection<User> collection =
                new MongoDBService()
                        .getMongoDatabase()
                        .getCollection(USER_COLLECTION_NAME, User.class);
        List<Long> subscribedUserIds = new ArrayList<>();
        FindIterable<User> subscribedUsers =
                collection.find(eq(USER_FIELD_NAME_SUBSCRIBE_STATUS, true));
        for (User user : subscribedUsers) {
            subscribedUserIds.add(user.getDiscordUserId());
        }
        return subscribedUserIds;
    }

    /**
     * Calculate the first execution delay between now and midnight.
     *
     * @return the first execution delay in seconds.
     */
    long calculateFirstExecutionDelay() {
        OffsetDateTime today = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime tomorrow =
                today.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return Duration.between(today, tomorrow).getSeconds();
    }

    /**
     * Push movie release notification to all the subscribed users through private messages.
     *
     * @param jda - Java Discord API.
     */
    void pushMovieReleaseNotification(JDA jda) {
        List<Long> subscribedUserIds = this.getSubscribedUserIds();
        if (subscribedUserIds.size() == 0) {
            return;
        }
        List<MovieDb> topTenMovies = this.fetchTopTenMovieReleaseInfo();
        if (topTenMovies.size() == 0) {
            for (Long subscribedUserId : subscribedUserIds) {
                jda.openPrivateChannelById(subscribedUserId)
                        .queue(pvc -> pvc.sendMessage(MSG_MOVIE_RELEASE_NOT_FOUND).queue());
                return;
            }
        }
        ArrayList<Page> moviePages = new ArrayList<>();
        for (MovieDb movie : topTenMovies) {
            Message movieMessage = MovieMessageBuilder.fromMovieDb(movie);
            moviePages.add(new InteractPage(movieMessage));
        }
        for (Long subscribedUserId : subscribedUserIds) {
            RestAction<PrivateChannel> channel = jda.openPrivateChannelById(subscribedUserId);
            channel.queue(
                    pvc ->
                            pvc.sendMessage((Message) moviePages.get(0).getContent())
                                    .queue(success -> Pages.paginate(success, moviePages, true)));
        }
    }

    /**
     * Schedule the notification to push every day at UTC midnight.
     *
     * @param jda - Java Discord API.
     */
    public void scheduleDailyPushNotification(JDA jda) {
        Runnable pushNotification = () -> this.pushMovieReleaseNotification(jda);
        // Change the interval of execution from 1 day to 30 seconds for demo only and need to be
        // changed back before final release.
        // SUBSCRIBE_EXECUTOR.scheduleAtFixedRate(sendNotifications,
        // this.calculateFirstExecutionDelay(), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
        SUBSCRIPTION_EXECUTOR.scheduleAtFixedRate(pushNotification, 0, 30, TimeUnit.SECONDS);
    }
}