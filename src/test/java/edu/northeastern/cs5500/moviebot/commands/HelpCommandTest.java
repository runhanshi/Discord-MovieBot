package edu.northeastern.cs5500.moviebot.commands;

import static edu.northeastern.cs5500.moviebot.commands.HelpCommand.INSTRUCTION_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.northeastern.cs5500.moviebot.listeners.MessageListener;
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
public class HelpCommandTest {
    private HelpCommand helpCommand;

    @Test
    public void getCommandConfiguration() {
        helpCommand = new HelpCommand();
        assertEquals(helpCommand.getCommandConfiguration().getName(), "help");
        assertEquals(
                helpCommand.getCommandConfiguration().getDescription(),
                "Show the instruction of the movie bot");
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendInstructionMessageSuccessfully() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        ReplyAction mockMessageAction = mock(ReplyAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        SlashCommandEvent mockCommandEvent = mock(SlashCommandEvent.class);
        when(mockCommandEvent.getName()).thenReturn("help");

        when(mockCommandEvent.reply(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
        MessageListener mockMessageListener = new MessageListener();
        mockMessageListener.onSlashCommand(mockCommandEvent);

        verify(mockCommandEvent, Mockito.times(1)).getName();
        assertEquals(INSTRUCTION_MESSAGE, stringArgumentCaptor.getAllValues().get(0).toString());
    }

    @Test
    public void getName() {
        helpCommand = new HelpCommand();
        assertEquals(helpCommand.getName(), "help");
    }
}
