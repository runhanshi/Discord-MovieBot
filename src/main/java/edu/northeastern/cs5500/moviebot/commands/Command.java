package edu.northeastern.cs5500.moviebot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface Command {

    public CommandData getCommandConfiguration();

    public void onSlashCommand(SlashCommandEvent event);

    public String getName();
}
