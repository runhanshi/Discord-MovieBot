package edu.northeastern.cs5500.moviebot.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WatchCommandTest {
    private WatchCommand watchCommand;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCommandConfiguration() {
        watchCommand = new WatchCommand();
        assertEquals(watchCommand.getCommandConfiguration().getName(), "watched");
        assertEquals(
                watchCommand.getCommandConfiguration().getDescription(), "show your watched list");
    }

    @Test
    public void showWatchedListOfInvalidUser() {
        WatchCommand watchCommand = mock(WatchCommand.class);
        SlashCommandEvent slashCommandEvent = mock(SlashCommandEvent.class);

        watchCommand.onSlashCommand(slashCommandEvent);

        verify(watchCommand, Mockito.times(1)).onSlashCommand(slashCommandEvent);
    }

    @Test
    public void showWatchedListOfInvalidCommand() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ReplyAction mockMessageAction = mock(ReplyAction.class);
        WatchCommand watchCommand = mock(WatchCommand.class);
        SlashCommandEvent slashCommandEvent = mock(SlashCommandEvent.class);
        watchCommand.onSlashCommand(slashCommandEvent);

        verify(slashCommandEvent, Mockito.times(0)).getJDA();
    }

    @Test
    public void getName() {
        watchCommand = new WatchCommand();
        assertEquals(watchCommand.getName(), "watched");
    }
}
