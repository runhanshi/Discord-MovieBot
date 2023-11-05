package edu.northeastern.cs5500.moviebot.commands;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.model.User;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public abstract class AbstractSubscribeCommand implements Command {

    static final String USER_COLLECTION_NAME = "User";
    static final String USER_FIELD_NAME_DISCORD_ID = "discordUserId";
    static final String USER_FIELD_NAME_SUBSCRIBE_STATUS = "subscribed";

    /**
     * Connect to the user collection stored in MongoDb.
     *
     * @return the user collection stored in MongoDb.
     */
    MongoCollection<User> connectToUserCollection() {
        return new MongoDBService()
                .getMongoDatabase()
                .getCollection(USER_COLLECTION_NAME, User.class);
    }

    /**
     * Create and insert a new user into the MongoDb's user collection.
     *
     * @param discordUserId - discord user id of the new user.
     */
    void insertUserToDatabase(long discordUserId) {
        User newUser = new User();
        newUser.setDiscordUserId(discordUserId);
        newUser.setSubscribed(false);
        this.connectToUserCollection().insertOne(newUser);
    }

    /**
     * Fetch the subscribe status of a user from the MongoDb's user collection.
     *
     * @param event - slash command event occurred in the message channel.
     * @return true if the user is subscribed and false otherwise.
     */
    boolean getUserSubscribeStatus(SlashCommandEvent event) {
        long eventUserId = event.getUser().getIdLong();
        User targetUser =
                this.connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventUserId))
                        .first();
        if (targetUser == null) {
            this.insertUserToDatabase(eventUserId);
            return false;
        }
        return targetUser.isSubscribed();
    }
}
