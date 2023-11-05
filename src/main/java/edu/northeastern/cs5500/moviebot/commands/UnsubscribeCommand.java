package edu.northeastern.cs5500.moviebot.commands;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class UnsubscribeCommand extends AbstractSubscribeCommand {

    static final String NAME_UNSUBSCRIBE_COMMAND = "unsubscribe";
    static final String DESCRIPTION_UNSUBSCRIBE_COMMAND =
            "Unsubscribe to daily notification for movie releases";
    static final String MSG_UNSUBSCRIBE_SUCCESS = "We are sorry to see you go!";
    static final String MSG_UNSUBSCRIBE_FAILED = "Oops... Looks like you are not subscribed.";

    /**
     * Build an unsubscribe command.
     *
     * @return the unsubscribe command builder.
     */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData(NAME_UNSUBSCRIBE_COMMAND, DESCRIPTION_UNSUBSCRIBE_COMMAND);
    }

    /**
     * Put the response to an unsubscribe command event in the queue.
     *
     * @param event - unsubscribe command event occurred in the message channel.
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.reply(this.setUserAsUnsubscribed(event)).queue();
    }

    /**
     * Get the name of an unsubscribe command.
     *
     * @return the name of the unsubscribe command.
     */
    @Override
    public String getName() {
        return NAME_UNSUBSCRIBE_COMMAND;
    }

    /**
     * Update the subscribe status of a user as false in the MongoDb's user collection.
     *
     * @param event - unsubscribe command event occurred in the message channel.
     * @return a message indicates whether the update request is successful or unsuccessful.
     */
    String setUserAsUnsubscribed(SlashCommandEvent event) {
        if (!this.getUserSubscribeStatus(event)) {
            return MSG_UNSUBSCRIBE_FAILED;
        }
        long eventUserId = event.getUser().getIdLong();
        this.connectToUserCollection()
                .findOneAndUpdate(
                        eq(USER_FIELD_NAME_DISCORD_ID, eventUserId),
                        set(USER_FIELD_NAME_SUBSCRIBE_STATUS, false));
        return MSG_UNSUBSCRIBE_SUCCESS;
    }
}
