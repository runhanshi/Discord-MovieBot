package edu.northeastern.cs5500.moviebot.commands;

import edu.northeastern.cs5500.moviebot.function.RemoveFromWatched;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/** This class helps to build remove command for users to remove certain movie from watched list. */
public class RemoveCommand implements Command {
    /**
     * Build the remove command.
     *
     * @return the remove command builder.
     */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData("remove", "remove this movie from your watched history")
                .addSubcommands(
                        new SubcommandData("title", "remove movie by title")
                                .addOptions(
                                        new OptionData(
                                                        OptionType.STRING,
                                                        "movie_title",
                                                        "type the movie title")
                                                .setRequired(true)));
    }

    /** Get the movie title from message and remove it from database. */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        User user = event.getUser();
        long id = user.getIdLong();
        String movieTitle = event.getOption("movie_title").getAsString();

        event.reply(movieTitle + " has been successfully removed!").queue();
        new RemoveFromWatched(id, movieTitle);
    }

    /**
     * Get the name of this command.
     *
     * @return the name of this command.
     */
    @Override
    public String getName() {
        return "remove";
    }
}
