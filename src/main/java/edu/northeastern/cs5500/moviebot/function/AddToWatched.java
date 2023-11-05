package edu.northeastern.cs5500.moviebot.function;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import java.time.LocalDate;
import java.util.Objects;

/** This class helps to add a movie title into a user's watched list. */
public class AddToWatched {
    private static final String USER_COLLECTION_NAME = "User";
    private static final String USER_FIELD_NAME_DISCORD_ID = "discordUserId";

    /**
     * Store movieTitle and Date into user's watchedList.
     *
     * @param discordId user's unique discord id.
     * @param movieTitle movie's title that a user wants to add.
     * @param localDate current date.
     */
    public AddToWatched(long discordId, String movieTitle, LocalDate localDate) {
        MongoCollection<User> collection =
                new MongoDBService()
                        .getMongoDatabase()
                        .getCollection(USER_COLLECTION_NAME, User.class);
        User targetUser = collection.find(eq(USER_FIELD_NAME_DISCORD_ID, discordId)).first();

        if (Objects.isNull(targetUser)) {
            User new_user = new User();
            new_user.getWatchedList().put(movieTitle, localDate);
            new_user.setDiscordUserId(discordId);
            collection.insertOne(new_user);
        } else {
            if (targetUser.getWatchedList().containsKey(movieTitle)) {
                return;
            } else {
                targetUser.getWatchedList().put(movieTitle, localDate);
                collection.findOneAndReplace(eq(USER_FIELD_NAME_DISCORD_ID, discordId), targetUser);
            }
        }
    }
}
