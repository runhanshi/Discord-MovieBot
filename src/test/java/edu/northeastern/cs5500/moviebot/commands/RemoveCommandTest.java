package edu.northeastern.cs5500.moviebot.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RemoveCommandTest {

    private RemoveCommand removeCommand;

    @Before
    public void setUp() {
        removeCommand = new RemoveCommand();
    }

    @Test
    public void getCommandConfiguration() {
        assertEquals(removeCommand.getCommandConfiguration().getName(), "remove");
        assertEquals(
                removeCommand.getCommandConfiguration().getDescription(),
                "remove this movie from your watched history");
    }

    @Test
    public void onSlashCommand() throws Exception {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        ReplyAction action = Mockito.mock(ReplyAction.class);
        User userMock = Mockito.mock(User.class);
        long userDiscordId = -1;

        when(event.getUser()).thenReturn(userMock);
        when(userMock.getIdLong()).thenReturn(userDiscordId);

        when(event.getName()).thenReturn("remove");
        when(event.getSubcommandName()).thenReturn("title");

        OptionMapping option = Mockito.mock(OptionMapping.class);
        when(option.getAsString()).thenReturn("Harry Potter");
        when(event.getOption("movie_title")).thenReturn(option);

        when(event.reply((String) any())).thenReturn(action);

        removeCommand.onSlashCommand(event);
        verify(event.reply("Harry Potter" + " has been successfully removed!"));
    }

    @Test
    public void getName() {
        assertEquals(removeCommand.getName(), "remove");
    }
}
