package edu.northeastern.cs5500.moviebot.commands;

import static com.mongodb.client.model.Filters.eq;
import static edu.northeastern.cs5500.moviebot.commands.AbstractSubscribeCommand.USER_FIELD_NAME_DISCORD_ID;
import static edu.northeastern.cs5500.moviebot.commands.SubscribeCommand.DESCRIPTION_SUBSCRIBE_COMMAND;
import static edu.northeastern.cs5500.moviebot.commands.SubscribeCommand.MSG_SUBSCRIBE_FAILED;
import static edu.northeastern.cs5500.moviebot.commands.SubscribeCommand.MSG_SUBSCRIBE_SUCCESS;
import static edu.northeastern.cs5500.moviebot.commands.SubscribeCommand.NAME_SUBSCRIBE_COMMAND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.mongodb.client.MongoCollection;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SubscribeCommandTest {

    private SubscribeCommand subscribe;

    @Before
    public void setUp() {
        subscribe = new SubscribeCommand();
    }

    @Test
    public void getCommandConfiguration() {
        assertEquals(subscribe.getCommandConfiguration().getName(), NAME_SUBSCRIBE_COMMAND);
        assertEquals(
                subscribe.getCommandConfiguration().getDescription(),
                DESCRIPTION_SUBSCRIBE_COMMAND);
    }

    @Test
    public void onSlashCommand() throws Exception {
        SubscribeCommand subscribeMock = spy(SubscribeCommand.class);

        SlashCommandEvent eventSuccessMock = mock(SlashCommandEvent.class);
        ReplyAction actionSuccess = spy(ReplyAction.class);
        PowerMockito.doReturn(MSG_SUBSCRIBE_SUCCESS)
                .when(subscribeMock, "setUserAsSubscribed", eventSuccessMock);
        PowerMockito.doReturn(actionSuccess)
                .when(eventSuccessMock, "reply", ArgumentMatchers.anyString());

        // Verify event replies message for success when subscribe operation succeeds.
        subscribeMock.onSlashCommand(eventSuccessMock);
        verify(eventSuccessMock).reply(MSG_SUBSCRIBE_SUCCESS);

        SlashCommandEvent eventFailedMock = mock(SlashCommandEvent.class);
        ReplyAction actionFailed = spy(ReplyAction.class);
        PowerMockito.doReturn(MSG_SUBSCRIBE_FAILED)
                .when(subscribeMock, "setUserAsSubscribed", eventFailedMock);
        PowerMockito.doReturn(actionFailed)
                .when(eventFailedMock, "reply", ArgumentMatchers.anyString());

        // Verify event replies message for failure when subscribe operation failed.
        subscribeMock.onSlashCommand(eventFailedMock);
        verify(eventFailedMock).reply(MSG_SUBSCRIBE_FAILED);
    }

    @Test
    public void getName() {
        assertEquals(subscribe.getName(), NAME_SUBSCRIBE_COMMAND);
    }

    @Test
    public void connectToUserCollection() {
        MongoCollection<edu.northeastern.cs5500.moviebot.model.User> collection =
                subscribe.connectToUserCollection();
        assertNotNull(collection);
    }

    @Test
    public void insertUserToDatabase() {
        long newDiscordUserId = -1;

        // Verify this user is not in MongoDb's user collection
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());

        // Verify this user is now in MongoDb's user collection after the insertion
        subscribe.insertUserToDatabase(newDiscordUserId);
        assertNotNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());

        // Remove dummy data after the test
        subscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId));
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());
    }

    @Test
    public void getUserSubscribeStatus() throws Exception {
        SlashCommandEvent eventMock = mock(SlashCommandEvent.class);
        User userMock = mock(User.class);
        long eventDiscordUserId = -1;

        PowerMockito.doReturn(userMock).when(eventMock, "getUser");
        PowerMockito.doReturn(eventDiscordUserId).when(userMock, "getIdLong");

        // Get subscribe status of new user
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
        assertFalse(subscribe.getUserSubscribeStatus(eventMock));

        // Get subscribe status of unsubscribed return user
        assertFalse(subscribe.getUserSubscribeStatus(eventMock));

        // Get subscribe status of subscribed return user
        subscribe.setUserAsSubscribed(eventMock);
        assertTrue(subscribe.getUserSubscribeStatus(eventMock));

        // Remove dummy data after the test
        subscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId));
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
    }

    @Test
    public void setUserAsSubscribed() throws Exception {
        SlashCommandEvent eventMock = mock(SlashCommandEvent.class);
        User userMock = mock(User.class);
        long eventDiscordUserId = -1;

        PowerMockito.doReturn(userMock).when(eventMock, "getUser");
        PowerMockito.doReturn(eventDiscordUserId).when(userMock, "getIdLong");

        // Set subscribe status of new user as true
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
        assertEquals(MSG_SUBSCRIBE_SUCCESS, subscribe.setUserAsSubscribed(eventMock));

        // Set subscribe status of subscribed return user as true
        assertEquals(MSG_SUBSCRIBE_FAILED, subscribe.setUserAsSubscribed(eventMock));

        // Set subscribe status of unsubscribed return user as true
        new UnsubscribeCommand().setUserAsUnsubscribed(eventMock);
        assertFalse(subscribe.getUserSubscribeStatus(eventMock));
        assertEquals(MSG_SUBSCRIBE_SUCCESS, subscribe.setUserAsSubscribed(eventMock));

        // Remove dummy data after the test
        subscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId));
        assertNull(
                subscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
    }
}
