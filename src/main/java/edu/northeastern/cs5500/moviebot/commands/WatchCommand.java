package edu.northeastern.cs5500.moviebot.commands;

import edu.northeastern.cs5500.moviebot.function.ShowWatched;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/** This class helps to show a user's watched list. */
public class WatchCommand implements Command {

    static final String FAIL_MESSAGE = "Sorry, your watched list is empty.";
    static final String WAIT_MESSAGE = "I am looking for your watched list...Please wait a moment...";
    static final String WATCHED_LIST_TITLE = "This is your watched list:";

    /** Get the command configuration of onSlashCommand /watched */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData("watched", "show your watched list");
    }

    /**
     * Show the watched list of current user for the /watched command.
     *
     * @param event SlashCommandEvent /watched
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        User user = event.getUser();
        long id = user.getIdLong();
        ShowWatched showWatched = new ShowWatched();
        HashMap<String, String> watchedList = showWatched.showWatchedList(id);
        event.reply(WAIT_MESSAGE).queue();
        if (watchedList.isEmpty()) {
            event.getChannel().sendMessage(FAIL_MESSAGE).queue();
        } else {
            EmbedBuilder watchedBuilder = new EmbedBuilder();
            watchedBuilder.setTitle(WATCHED_LIST_TITLE);
            watchedBuilder.setColor(Color.YELLOW);
            for (Map.Entry<String, String> entry : watchedList.entrySet()) {
                watchedBuilder.addField(entry.getKey(), entry.getValue(), false);
            }
            event.getChannel().sendMessage(watchedBuilder.build()).queue();
        }
    }

    /** Get the name of onSlashCommand /watched. */
    @Override
    public String getName() {
        return "watched";
    }
}
