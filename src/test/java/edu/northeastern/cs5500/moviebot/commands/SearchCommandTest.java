package edu.northeastern.cs5500.moviebot.commands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.northeastern.cs5500.moviebot.App;
import edu.northeastern.cs5500.moviebot.messageBuilder.MovieMessageBuilder;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbFind;
import info.movito.themoviedbapi.model.FindResults;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SearchCommandTest {

    private Command test;

    @Before
    public void setUp() {
        test = new SearchCommand();
    }

    @Test
    public void getCommandConfiguration() {
        CommandData commandData = test.getCommandConfiguration();
        assertEquals(commandData.getName(), "search");
        assertEquals(commandData.getDescription(), "make the bot echo back the the ID/title/name provided for now");
    }

    @Test
    public void onSlashCommandIDInvalidID() {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        when(event.getName()).thenReturn("search");
        when(event.getSubcommandName()).thenReturn("id");

        OptionMapping option = Mockito.mock(OptionMapping.class);
        when(option.getAsString()).thenReturn("123456");
        when(event.getOption("movie_id")).thenReturn(option);

        ReplyAction action = Mockito.mock(ReplyAction.class);
        when(event.reply((String) any())).thenReturn(action);

        test.onSlashCommand(event);
        verify(event).reply("This ID 123456 is not found. Please try another ID.");
    }

    @Test
    public void onSlashCommandIDValidID() {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        when(event.getName()).thenReturn("search");
        when(event.getSubcommandName()).thenReturn("id");

        OptionMapping option = Mockito.mock(OptionMapping.class);
        when(option.getAsString()).thenReturn("tt0120737");
        when(event.getOption("movie_id")).thenReturn(option);

        ReplyAction action = Mockito.mock(ReplyAction.class);
        when(event.reply((Message) any())).thenReturn(action);

        test.onSlashCommand(event);
        TmdbApi api = new TmdbApi(App.getAPIKey());
        TmdbFind find = api.getFind();
        FindResults idResults = find.find("tt0120737", TmdbFind.ExternalSource.imdb_id, "en_US");
        verify(event).reply(MovieMessageBuilder.fromMovieDb(idResults.getMovieResults().get(0)));
    }

    @Test
    public void onSlashCommandTitleInvalidTitle() {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        when(event.getName()).thenReturn("search");
        when(event.getSubcommandName()).thenReturn("title");

        OptionMapping option = Mockito.mock(OptionMapping.class);
        when(event.getOption("movie_title")).thenReturn(option);
        when(option.getAsString()).thenReturn("fake movie name");

        ReplyAction action = Mockito.mock(ReplyAction.class);
        when(event.reply((String) any())).thenReturn(action);

        test.onSlashCommand(event);
        verify(event).reply("invalid movie title. Please try another one. ");
    }

    @Test
    public void onSlashCommandIDValidTitle() {
        SlashCommandEvent event = Mockito.mock(SlashCommandEvent.class);
        when(event.getName()).thenReturn("search");
        when(event.getSubcommandName()).thenReturn("id");

        OptionMapping option = Mockito.mock(OptionMapping.class);
        when(option.getAsString()).thenReturn("tt0120737");
        when(event.getOption("movie_id")).thenReturn(option);

        ReplyAction action = Mockito.mock(ReplyAction.class);
        when(event.reply((Message) any())).thenReturn(action);

        test.onSlashCommand(event);
        TmdbApi api = new TmdbApi(App.getAPIKey());
        TmdbFind find = api.getFind();
        FindResults idResults = find.find("tt0120737", TmdbFind.ExternalSource.imdb_id, "en_US");
        verify(event).reply(MovieMessageBuilder.fromMovieDb(idResults.getMovieResults().get(0)));
    }

    @Test
    public void getName() {
        assertEquals(test.getName(), "search");
    }
}
