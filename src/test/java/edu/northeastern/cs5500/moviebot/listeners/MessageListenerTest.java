package edu.northeastern.cs5500.moviebot.listeners;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MessageListenerTest {
    private MessageListener messageListener;

    @Before
    public void setUp() {
        messageListener = new MessageListener();
    }

    @Test
    public void onSlashCommand() {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        when(event.getName()).thenReturn("help");
        ReplyAction action = mock(ReplyAction.class);
        when(event.reply((String) any())).thenReturn(action);
        messageListener.onSlashCommand(event);
        verify(action).queue();
    }
}

