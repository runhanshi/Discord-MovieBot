package edu.northeastern.cs5500.moviebot.commands;

import edu.northeastern.cs5500.moviebot.function.AddToWatched;
import java.time.LocalDate;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/** This class helps to build add command for users to mark watched movies. */
public class AddCommand implements Command {

    /**
     * Build the add command.
     *
     * @return the add command builder.
     */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData("add", "add this movie to your watched history")
                .addSubcommands(
                        new SubcommandData("title", "add movie by movie title")
                                .addOptions(
                                        new OptionData(
                                                        OptionType.STRING,
                                                        "movie_title",
                                                        "type the movie title")
                                                .setRequired(true)));
    }

    @Override
    /** Get the movie title from message and saved into database. */
    public void onSlashCommand(SlashCommandEvent event) {
        User user = event.getUser();
        long id = user.getIdLong();
        String movieTitle = event.getOption("movie_title").getAsString();
        event.reply(movieTitle + " has been successfully added!").queue();
        new AddToWatched(id, movieTitle, LocalDate.now());
    }

    /**
     * Get the name of this command.
     *
     * @return the name of this command.
     */
    @Override
    public String getName() {
        return "add";
    }
}