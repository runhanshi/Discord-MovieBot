package edu.northeastern.cs5500.moviebot.function;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** This class helps to show a user's watched list. */
public class ShowWatched {
    static final String USER_COLLECTION_NAME = "User";
    static final String USER_FIELD_NAME_DISCORD_ID = "discordUserId";

    /** Constructor of showWatched. */
    public ShowWatched() {}

    /**
     * Connect to the mongoDB, get the watched list of current user.
     *
     * @param discordId - long, the discordId of current user
     * @return - a hashmap contains watched list content.
     */
    public HashMap<String, String> showWatchedList(long discordId) {
        MongoCollection<User> collection =
                new MongoDBService()
                        .getMongoDatabase()
                        .getCollection(USER_COLLECTION_NAME, User.class);
        User targetUser = collection.find(eq(USER_FIELD_NAME_DISCORD_ID, discordId)).first();

        HashMap<String, String> movieInfo = new HashMap<>();
        if (!Objects.isNull(targetUser)) {
            Map<String, LocalDate> watchedList = targetUser.getWatchedList();
            if (watchedList != null && watchedList.size() > 0) {
                Set<String> movieTitles = watchedList.keySet();
                for (String movieTitle : movieTitles) {
                    LocalDate localDate = watchedList.get(movieTitle);
                    String time = "";
                    if (localDate != null) {
                        time = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    movieInfo.put(movieTitle, "watched on " + time);
                }
            }
        }
        return movieInfo;
    }
}
