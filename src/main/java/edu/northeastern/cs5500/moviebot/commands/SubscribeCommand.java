package edu.northeastern.cs5500.moviebot.commands;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SubscribeCommand extends AbstractSubscribeCommand {

    static final String NAME_SUBSCRIBE_COMMAND = "subscribe";
    static final String DESCRIPTION_SUBSCRIBE_COMMAND =
            "Subscribe to daily notification for movie releases";
    static final String MSG_SUBSCRIBE_SUCCESS = "Thank you for subscribing!";
    static final String MSG_SUBSCRIBE_FAILED = "Oops... Looks like you are already subscribed.";

    /**
     * Build a subscribe command.
     *
     * @return the subscribe command builder.
     */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData(NAME_SUBSCRIBE_COMMAND, DESCRIPTION_SUBSCRIBE_COMMAND);
    }

    /**
     * Put the response to a subscribe command event in the queue.
     *
     * @param event - subscribe command event occurred in the message channel.
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.reply(this.setUserAsSubscribed(event)).queue();
    }

    /**
     * Get the name of a subscribe command.
     *
     * @return the name of the subscribe command.
     */
    @Override
    public String getName() {
        return NAME_SUBSCRIBE_COMMAND;
    }

    /**
     * Update the subscribe status of a user as true in the MongoDb's user collection.
     *
     * @param event - subscribe command event occurred in the message channel.
     * @return a message indicates whether the update request is successful or unsuccessful.
     */
    String setUserAsSubscribed(SlashCommandEvent event) {
        if (this.getUserSubscribeStatus(event)) {
            return MSG_SUBSCRIBE_FAILED;
        }
        long eventUserId = event.getUser().getIdLong();
        this.connectToUserCollection()
                .findOneAndUpdate(
                        eq(USER_FIELD_NAME_DISCORD_ID, eventUserId),
                        set(USER_FIELD_NAME_SUBSCRIBE_STATUS, true));
        return MSG_SUBSCRIBE_SUCCESS;
    }
}
