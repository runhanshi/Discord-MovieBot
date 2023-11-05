package edu.northeastern.cs5500.moviebot.subscriptionManager;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mongodb.client.MongoCollection;
import edu.northeastern.cs5500.moviebot.commands.SubscribeCommand;
import edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand;
import edu.northeastern.cs5500.moviebot.service.MongoDBService;
import java.util.ArrayList;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class SubscriptionManagerTest {

    private SubscriptionManager manager;

    @Before
    public void setUp() {
        manager = new SubscriptionManager();
    }

    @Test
    public void generateMovieReleaseDate() {
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        assertTrue(datePattern.matcher(manager.generateMovieReleaseDate()).matches());
    }

    @Test
    public void constructApiRequestUrl() {
        assertNotNull(manager.constructApiRequestUrl());
    }

    @Test
    public void fetchTopTenMovieReleaseInfo() {
        assertTrue(manager.fetchTopTenMovieReleaseInfo().size() <= 10);
    }

    @Test
    public void getSubscribedUserIds() throws Exception {
        SubscribeCommand subscribeMock = spy(SubscribeCommand.class);
        SlashCommandEvent subscribeEventMock = mock(SlashCommandEvent.class);
        User subscribeUserMock = mock(User.class);
        long subscribeUserId = -1;
        ReplyAction actionSubscribe = spy(ReplyAction.class);
        PowerMockito.doReturn(subscribeUserMock).when(subscribeEventMock, "getUser");
        PowerMockito.doReturn(subscribeUserId).when(subscribeUserMock, "getIdLong");
        PowerMockito.doReturn(actionSubscribe)
                .when(subscribeEventMock, "reply", ArgumentMatchers.anyString());
        subscribeMock.onSlashCommand(subscribeEventMock);

        // Verify the result contains a subscribed user id.
        assertTrue(manager.getSubscribedUserIds().contains(subscribeUserId));

        UnsubscribeCommand unsubscribeMock = spy(UnsubscribeCommand.class);
        SlashCommandEvent unsubscribeEventMock = mock(SlashCommandEvent.class);
        User unsubscribeUserMock = mock(User.class);
        long unsubscribeUserId = -2;
        ReplyAction actionUnsubscribe = spy(ReplyAction.class);
        PowerMockito.doReturn(unsubscribeUserMock).when(unsubscribeEventMock, "getUser");
        PowerMockito.doReturn(unsubscribeUserId).when(unsubscribeUserMock, "getIdLong");
        PowerMockito.doReturn(actionUnsubscribe)
                .when(unsubscribeEventMock, "reply", ArgumentMatchers.anyString());
        unsubscribeMock.onSlashCommand(unsubscribeEventMock);

        // Verify the result doesn't contain an unsubscribed user id.
        assertFalse(manager.getSubscribedUserIds().contains(unsubscribeUserId));

        // Remove dummy data after the test
        MongoCollection<edu.northeastern.cs5500.moviebot.model.User> collection =
                new MongoDBService()
                        .getMongoDatabase()
                        .getCollection("User", edu.northeastern.cs5500.moviebot.model.User.class);
        collection.deleteOne(eq("discordUserId", subscribeUserId));
        collection.deleteOne(eq("discordUserId", unsubscribeUserId));
    }

    @Test
    public void calculateFirstExecutionDelay() {
        assertTrue(manager.calculateFirstExecutionDelay() >= 0);
    }

    @Test
    public void pushMovieReleaseNotificationWhenNoSubscriber() throws Exception {
        SubscriptionManager managerMock = spy(SubscriptionManager.class);
        JDA jdaMock = spy(JDA.class);
        PowerMockito.doReturn(new ArrayList<>()).when(managerMock, "getSubscribedUserIds");
        managerMock.pushMovieReleaseNotification(jdaMock);
        verify(managerMock, times(0)).fetchTopTenMovieReleaseInfo();
    }

    @Test
    public void pushMovieReleaseNotificationWhenNoMovieRelease() throws Exception {
        SubscriptionManager managerMock = spy(SubscriptionManager.class);
        JDA jdaMock = spy(JDA.class);
        PowerMockito.doReturn(
                        new ArrayList<Long>() {
                            {
                                add(-1L);
                            }
                        })
                .when(managerMock, "getSubscribedUserIds");
        PowerMockito.doReturn(new ArrayList<>()).when(managerMock, "fetchTopTenMovieReleaseInfo");
        RestAction<PrivateChannel> channelMock = spy(RestAction.class);
        PowerMockito.doReturn(channelMock)
                .when(jdaMock, "openPrivateChannelById", ArgumentMatchers.anyLong());
        PowerMockito.doNothing().when(channelMock, "queue", ArgumentMatchers.any());
        managerMock.pushMovieReleaseNotification(jdaMock);
        verify(managerMock, times(1)).fetchTopTenMovieReleaseInfo();

        // Remove dummy data after the test
        MongoCollection<edu.northeastern.cs5500.moviebot.model.User> collection =
            new MongoDBService()
                .getMongoDatabase()
                .getCollection("User", edu.northeastern.cs5500.moviebot.model.User.class);
        collection.deleteOne(eq("discordUserId", -1L));
    }
}
