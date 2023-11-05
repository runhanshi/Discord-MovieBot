package edu.northeastern.cs5500.moviebot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/** This class helps to show the help message of bot. */
public class HelpCommand implements Command {
    static final String INSTRUCTION_MESSAGE =
        "Instruction of the movie bot: \n"
            + "Please input the corresponding keywords: \n"
            + "/search id [movieID]: search movies by movieID \n"
            + "/search title [movieTitle]: search movies by movieTitle \n"
            + "/subscribe: grant the authorization to receive the notification of new released movie \n"
            + "/unsubscribe: revoke the authorization to receive the notification of new released movie \n"
            + "/help: get the instructions of the movieBot \n"
            + "/add title [movieTitle]: add the movie to your watched list via movieTitle \n"
            + "/remove title [movieTitle]: remove the movie from your watched list via movieTitle \n"
            + "/watched: see your watched list \n";

    /** Get the command configuration of onSlashCommand /help */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData("help", "Show the instruction of the movie bot");
    }

    /**
     * Reply the instructionMessage to the /help command.
     *
     * @param event SlashCommandEvent /help
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.reply(INSTRUCTION_MESSAGE).queue();
    }

    /** Get the name of onSlashCommand /help. */
    @Override
    public String getName() {
        return "help";
    }
}
