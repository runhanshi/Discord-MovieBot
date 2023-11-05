package edu.northeastern.cs5500.moviebot.commands;

import static com.mongodb.client.model.Filters.eq;
import static edu.northeastern.cs5500.moviebot.commands.AbstractSubscribeCommand.USER_FIELD_NAME_DISCORD_ID;
import static edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand.DESCRIPTION_UNSUBSCRIBE_COMMAND;
import static edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand.MSG_UNSUBSCRIBE_FAILED;
import static edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand.MSG_UNSUBSCRIBE_SUCCESS;
import static edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand.NAME_UNSUBSCRIBE_COMMAND;
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
public class UnsubscribeCommandTest {

    private UnsubscribeCommand unsubscribe;

    @Before
    public void setUp() {
        unsubscribe = new UnsubscribeCommand();
    }

    @Test
    public void getCommandConfiguration() {
        assertEquals(unsubscribe.getCommandConfiguration().getName(), NAME_UNSUBSCRIBE_COMMAND);
        assertEquals(
                unsubscribe.getCommandConfiguration().getDescription(),
                DESCRIPTION_UNSUBSCRIBE_COMMAND);
    }

    @Test
    public void onSlashCommand() throws Exception {
        UnsubscribeCommand unsubscribeMock = spy(UnsubscribeCommand.class);

        SlashCommandEvent eventSuccessMock = mock(SlashCommandEvent.class);
        ReplyAction actionSuccess = spy(ReplyAction.class);
        PowerMockito.doReturn(MSG_UNSUBSCRIBE_SUCCESS)
                .when(unsubscribeMock, "setUserAsUnsubscribed", eventSuccessMock);
        PowerMockito.doReturn(actionSuccess)
                .when(eventSuccessMock, "reply", ArgumentMatchers.anyString());

        // Verify event replies message for success when unsubscribe operation succeeds.
        unsubscribeMock.onSlashCommand(eventSuccessMock);
        verify(eventSuccessMock).reply(MSG_UNSUBSCRIBE_SUCCESS);

        SlashCommandEvent eventFailedMock = mock(SlashCommandEvent.class);
        ReplyAction actionFailed = spy(ReplyAction.class);
        PowerMockito.doReturn(MSG_UNSUBSCRIBE_FAILED)
                .when(unsubscribeMock, "setUserAsUnsubscribed", eventFailedMock);
        PowerMockito.doReturn(actionFailed)
                .when(eventFailedMock, "reply", ArgumentMatchers.anyString());

        // Verify event replies message for failure when unsubscribe operation failed.
        unsubscribeMock.onSlashCommand(eventFailedMock);
        verify(eventFailedMock).reply(MSG_UNSUBSCRIBE_FAILED);
    }

    @Test
    public void getName() {
        assertEquals(unsubscribe.getName(), NAME_UNSUBSCRIBE_COMMAND);
    }

    @Test
    public void connectToUserCollection() {
        MongoCollection<edu.northeastern.cs5500.moviebot.model.User> collection =
                unsubscribe.connectToUserCollection();
        assertNotNull(collection);
    }

    @Test
    public void insertUserToDatabase() {
        long newDiscordUserId = -2;

        // Verify this user is not in MongoDb's user collection
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());

        // Verify this user is now in MongoDb's user collection after the insertion
        unsubscribe.insertUserToDatabase(newDiscordUserId);
        assertNotNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());

        // Remove dummy data after the test
        unsubscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId));
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, newDiscordUserId))
                        .first());
    }

    @Test
    public void getUserSubscribeStatus() throws Exception {
        SlashCommandEvent eventMock = mock(SlashCommandEvent.class);
        User userMock = mock(User.class);
        long eventDiscordUserId = -2;

        PowerMockito.doReturn(userMock).when(eventMock, "getUser");
        PowerMockito.doReturn(eventDiscordUserId).when(userMock, "getIdLong");

        // Get subscribe status of new user
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
        assertFalse(unsubscribe.getUserSubscribeStatus(eventMock));

        // Get subscribe status of unsubscribed return user
        assertFalse(unsubscribe.getUserSubscribeStatus(eventMock));

        // Get subscribe status of subscribed return user
        new SubscribeCommand().setUserAsSubscribed(eventMock);
        assertTrue(unsubscribe.getUserSubscribeStatus(eventMock));

        // Remove dummy data after the test
        unsubscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId));
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
    }

    @Test
    public void setUserAsUnsubscribed() throws Exception {
        SlashCommandEvent eventMock = mock(SlashCommandEvent.class);
        User userMock = mock(User.class);
        long eventDiscordUserId = -2;

        PowerMockito.doReturn(userMock).when(eventMock, "getUser");
        PowerMockito.doReturn(eventDiscordUserId).when(userMock, "getIdLong");

        // Set subscribe status of new user as false
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
        assertEquals(MSG_UNSUBSCRIBE_FAILED, unsubscribe.setUserAsUnsubscribed(eventMock));

        // Set subscribe status of unsubscribed return user as false
        assertEquals(MSG_UNSUBSCRIBE_FAILED, unsubscribe.setUserAsUnsubscribed(eventMock));

        // Set subscribe status of subscribed return user as false
        new SubscribeCommand().setUserAsSubscribed(eventMock);
        assertTrue(unsubscribe.getUserSubscribeStatus(eventMock));
        assertEquals(MSG_UNSUBSCRIBE_SUCCESS, unsubscribe.setUserAsUnsubscribed(eventMock));

        // Remove dummy data after the test
        unsubscribe
                .connectToUserCollection()
                .deleteOne(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId));
        assertNull(
                unsubscribe
                        .connectToUserCollection()
                        .find(eq(USER_FIELD_NAME_DISCORD_ID, eventDiscordUserId))
                        .first());
    }
}
