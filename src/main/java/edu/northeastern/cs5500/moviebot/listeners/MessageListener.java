package edu.northeastern.cs5500.moviebot.listeners;

import edu.northeastern.cs5500.moviebot.commands.AddCommand;
import edu.northeastern.cs5500.moviebot.commands.Command;
import edu.northeastern.cs5500.moviebot.commands.HelpCommand;
import edu.northeastern.cs5500.moviebot.commands.RemoveCommand;
import edu.northeastern.cs5500.moviebot.commands.SearchCommand;
import edu.northeastern.cs5500.moviebot.commands.SubscribeCommand;
import edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand;
import edu.northeastern.cs5500.moviebot.commands.WatchCommand;
import edu.northeastern.cs5500.moviebot.commands.SubscribeCommand;
import edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/** This class helps to listening the message of bot. */
public class MessageListener extends ListenerAdapter {
    private Map<String, Command> commands;

    /** Construct a new MessageListener(). Add valid command. */
    public MessageListener() {
        commands = new HashMap<>();
        commands.put("help", new HelpCommand());
        commands.put("search", new SearchCommand());
        commands.put("subscribe", new SubscribeCommand());
        commands.put("unsubscribe", new UnsubscribeCommand());
        commands.put("add", new AddCommand());
        commands.put("remove", new RemoveCommand());
        commands.put("watched", new WatchCommand());
    }

    /**
     * Get the name of onSlashCommand.
     *
     * @param event SlashCommandEvent
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        commands.get(event.getName()).onSlashCommand(event);
    }
}
