package edu.northeastern.cs5500.moviebot.listeners;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.northeastern.cs5500.moviebot.App;
import edu.northeastern.cs5500.moviebot.messageBuilder.MovieMessageBuilder;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;
import org.junit.Before;
import org.junit.Test;

public class ButtonListenerTest {
    private ButtonListener buttonListener;
    private TmdbApi api;

    @Before
    public void setUp() {
        buttonListener = new ButtonListener();
        api = new TmdbApi(App.getAPIKey());
    }

    @Test
    public void onButtonClickPrev() {
        ButtonClickEvent event = mock(ButtonClickEvent.class);
        when(event.getComponentId()).thenReturn("prev");
        MessageChannel channel = mock(MessageChannel.class);
        when(event.getMessageChannel()).thenReturn(channel);
        when(channel.getId()).thenReturn("12345");
        when(event.editMessageEmbeds((MessageEmbed) any()))
                .thenReturn(mock(UpdateInteractionAction.class));

        TmdbSearch search = api.getSearch();
        MovieResultsPage titleResults = search.searchMovie("harry potter", 0, "en_US", true, 1);

        List<MovieDb> movieList = titleResults.getResults();

        buttonListener.addCommunication("12345", movieList);
        buttonListener.onButtonClick(event);
        verify(event).editMessageEmbeds(MovieMessageBuilder.embedFromMovieDb(movieList.get(0)));
    }

    @Test
    public void onButtonClickNext() {
        ButtonClickEvent event = mock(ButtonClickEvent.class);
        when(event.getComponentId()).thenReturn("next");
        MessageChannel channel = mock(MessageChannel.class);
        when(event.getMessageChannel()).thenReturn(channel);
        when(channel.getId()).thenReturn("12345");
        when(event.editMessageEmbeds((MessageEmbed) any()))
                .thenReturn(mock(UpdateInteractionAction.class));

        TmdbSearch search = api.getSearch();
        MovieResultsPage titleResults = search.searchMovie("harry potter", 0, "en_US", true, 1);

        List<MovieDb> movieList = titleResults.getResults();

        buttonListener.addCommunication("12345", movieList);
        buttonListener.onButtonClick(event);
        verify(event).editMessageEmbeds(MovieMessageBuilder.embedFromMovieDb(movieList.get(1)));
    }

    @Test
    public void addCommunication() {
        buttonListener.addCommunication("test", new ArrayList<>());
        assertEquals(buttonListener.getChannelIDToState().size(), 1);
    }
}
