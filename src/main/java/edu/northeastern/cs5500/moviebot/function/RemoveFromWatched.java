package edu.northeastern.cs5500.moviebot.function;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import java.util.Objects;

/** This class helps to remove a movie title from a user's watched list. */
public class RemoveFromWatched {
    private static final String USER_COLLECTION_NAME = "User";
    private static final String USER_FIELD_NAME_DISCORD_ID = "discordUserId";

    /**
     * Remove a movie data from user's watchedList.
     *
     * @param discordId user's unique discord id.
     * @param movieTitle movie's title that a user wants to remove.
     */
    public RemoveFromWatched(long discordId, String movieTitle) {
        MongoCollection<User> collection =
                new MongoDBService()
                        .getMongoDatabase()
                        .getCollection(USER_COLLECTION_NAME, User.class);
        User targetUser = collection.find(eq(USER_FIELD_NAME_DISCORD_ID, discordId)).first();

        if (Objects.isNull(targetUser)) {
            return;
        } else {
            if (targetUser.getWatchedList().containsKey(movieTitle)) {
                targetUser.getWatchedList().remove(movieTitle);
                collection.findOneAndReplace(eq(USER_FIELD_NAME_DISCORD_ID, discordId), targetUser);
            } else {
                return;
            }
        }
    }
}
