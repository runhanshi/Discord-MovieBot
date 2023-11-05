package edu.northeastern.cs5500.moviebot.listeners;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.entities.SelfUserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrivateMessageListenerTest {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPrivateMessageFromBot() {
        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(true);

        PrivateMessageReceivedEvent mockCommandEvent = mock(PrivateMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onPrivateMessageReceived(mockCommandEvent);

        verify(mockCommandEvent).getAuthor();
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        assertEquals(0, stringArgumentCaptor.getAllValues().size());
    }

    @Test
    public void testSendPrivateMessageSuccessfully() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(false);

        RestAction mockRestAction = mock(RestAction.class);
        Message message = mock(Message.class);
        MessageHistory messageHistory = mock(MessageHistory.class);
        List<Message> list = new ArrayList<>();
        list.add(message);
        when(messageHistory.retrievePast(100)).thenReturn(mockRestAction);
        when(messageHistory.retrievePast(100).complete()).thenReturn(list);
        PrivateChannel mockTextChannel = mock(PrivateChannel.class);
        when(mockTextChannel.getHistory()).thenReturn(messageHistory);

        PrivateMessageReceivedEvent mockCommandEvent = mock(PrivateMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture()))
            .thenReturn(mockMessageAction);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onPrivateMessageReceived(mockCommandEvent);

        verify(mockCommandEvent, Mockito.times(2)).getChannel();
        assertEquals(
            "Hi! This is MovieBot! Please enter /help to get my instructions!",
            stringArgumentCaptor.getAllValues().get(0).toString());
    }

    @Test
    public void testPrivateMessageExceedTwo() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(false);

        RestAction mockRestAction = mock(RestAction.class);
        Message message = mock(Message.class);
        MessageHistory messageHistory = mock(MessageHistory.class);
        List<Message> list = new ArrayList<>();
        list.add(message);
        list.add(message);
        list.add(message);
        when(messageHistory.retrievePast(100)).thenReturn(mockRestAction);
        when(messageHistory.retrievePast(100).complete()).thenReturn(list);
        PrivateChannel mockTextChannel = mock(PrivateChannel.class);
        when(mockTextChannel.getHistory()).thenReturn(messageHistory);

        PrivateMessageReceivedEvent mockCommandEvent = mock(PrivateMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onPrivateMessageReceived(mockCommandEvent);

        verify(mockCommandEvent, Mockito.times(1)).getChannel();
        assertEquals(0, stringArgumentCaptor.getAllValues().size());
    }

    @Test
    public void testGuildJoinSuccessfully() {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        Guild guild = mock(Guild.class);
        TextChannel mockTextChannel = mock(TextChannel.class);
        when(guild.getDefaultChannel()).thenReturn(mockTextChannel);

        GuildJoinEvent mockCommandEvent = mock(GuildJoinEvent.class);

        when(mockCommandEvent.getGuild()).thenReturn(guild);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture()))
            .thenReturn(mockMessageAction);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onGuildJoin(mockCommandEvent);

        verify(mockCommandEvent, Mockito.times(1)).getGuild();
        assertEquals(
            "Hi! This is MovieBot!", stringArgumentCaptor.getAllValues().get(0).toString());
    }

    @Test
    public void testOnGuildMessageReceivedFromBot() {
        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(true);

        GuildMessageReceivedEvent mockCommandEvent = mock(GuildMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onGuildMessageReceived(mockCommandEvent);

        verify(mockCommandEvent).getAuthor();
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        assertEquals(0, stringArgumentCaptor.getAllValues().size());
    }

    @Test
    public void testOnGuildMessageReceivedEqualsNull() {
        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(false);
        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("");
        GuildMessageReceivedEvent mockCommandEvent = mock(GuildMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);
        when(mockCommandEvent.getMessage()).thenReturn(message);
        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onGuildMessageReceived(mockCommandEvent);

        verify(mockCommandEvent).getAuthor();
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        assertEquals(0, stringArgumentCaptor.getAllValues().size());
    }

    @Test
    public void testOnGuildMessageReceivedSuccessfully() {

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(false);
        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("/help");
        GuildMessageReceivedEvent mockCommandEvent = mock(GuildMessageReceivedEvent.class);
        when(mockCommandEvent.getAuthor()).thenReturn(bot);
        when(mockCommandEvent.getMessage()).thenReturn(message);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onGuildMessageReceived(mockCommandEvent);

        verify(mockCommandEvent, Mockito.times(2)).getAuthor();
        assertEquals(0, stringArgumentCaptor.getAllValues().size());
    }

    @Test
    public void testOnGuildMessageReceivedWrongFormat() {

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        SelfUserImpl bot = mock(SelfUserImpl.class);
        when(bot.isBot()).thenReturn(false);
        Message message = mock(Message.class);
        when(message.getContentRaw()).thenReturn("help");
        GuildMessageReceivedEvent mockCommandEvent = mock(GuildMessageReceivedEvent.class);

        TextChannel mockTextChannel = mock(TextChannel.class);

        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture()))
            .thenReturn(mockMessageAction);

        when(mockCommandEvent.getAuthor()).thenReturn(bot);
        when(mockCommandEvent.getMessage()).thenReturn(message);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);

        PrivateMessageListener mockPrivateMessageListener = new PrivateMessageListener();
        mockPrivateMessageListener.onGuildMessageReceived(mockCommandEvent);
        verify(mockCommandEvent, Mockito.times(1)).getAuthor();
        assertEquals(
            "I can't handle that command right now :( \n"
                + " Please enter /help to get my instructions.",
            stringArgumentCaptor.getAllValues().get(0));
        assertEquals(1, stringArgumentCaptor.getAllValues().size());
    }
}
