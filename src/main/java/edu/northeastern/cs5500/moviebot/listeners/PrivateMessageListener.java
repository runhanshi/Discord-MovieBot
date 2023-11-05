package edu.northeastern.cs5500.moviebot.listeners;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/** This class helps to listening the private message. */
public class PrivateMessageListener extends ListenerAdapter {

    static final String PRIVATE_RESPONSE = "Hi! This is MovieBot! Please enter /help to get my instructions!";
    static final String WELCOME_MESSAGE = "Hi! This is MovieBot!";
    static final String HELP_MESSAGE = "I can't handle that command right now :( \n "
        + "Please enter /help to get my instructions.";


    /**
     * Send a instruction message to user if the user send a private message to the bot.
     *
     * @param event PrivateMessageReceivedEvent, when the bot received a private message.
     */
    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getChannel().getHistory().retrievePast(100).complete().size() < 2) {
            event.getChannel()
                .sendMessage(PRIVATE_RESPONSE)
                .queue();
        }
    }

    /**
     * Bot send a welcome message to user if it join to a guild.
     *
     * @param event GuildJoinEvent, the bot join to a guild.
     */
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        TextChannel textChannel = event.getGuild().getDefaultChannel();
        textChannel.sendMessage(WELCOME_MESSAGE).queue();
    }

    /**
     * Send a warning message to user if the user send an invalid command to the bot.
     *
     * @param event GuildMessageReceivedEvent, when the bot received a guild message.
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (event.getMessage().getContentRaw().equals("")) {
            return;
        }
        if (!this.checkPrefix(event)) {
            event.getChannel()
                .sendMessage(
                    HELP_MESSAGE)
                .queue();
            return;
        }
    }

    /**
     * Help function. Check the prefix of onSlashCommand.
     *
     * @param event PrivateMessageReceivedEvent
     * @return whether or not the command is valid. return true if valid, otherwise, return false.
     */
    private boolean checkPrefix(GuildMessageReceivedEvent event) {
        Set<String> command_name = new HashSet<>();
        command_name.add("/help");
        command_name.add("/search");
        command_name.add("/subscribe");
        command_name.add("/unsubscribe");
        command_name.add("/add");
        command_name.add("/remove");
        command_name.add("/watched");

        String[] args = event.getMessage().getContentRaw().split(" ");
        if (!args[0].startsWith("/")
            || event.getAuthor().isBot()
            || !command_name.contains((args[0]))) {
            return false;
        }
        return true;
    }
}
