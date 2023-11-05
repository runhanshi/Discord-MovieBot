package edu.northeastern.cs5500.moviebot;

import static org.junit.Assert.*;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import org.junit.Before;
import org.junit.Test;

public class AppTest {
    private TmdbApi api;

    @Before
    public void setUp() {
        String key = App.getAPIKey();
        api = new TmdbApi(key);
    }

    @Test
    public void getBotToken() {
        assertNotNull(App.getBotToken());
    }

    @Test
    public void getAPIKey() {
        assertNotNull(App.getAPIKey());
    }

    @Test
    public void testTMDBWrapperIsWorking() {
        TmdbMovies movies = api.getMovies();
        assertNotNull(movies.getLatestMovie());
    }
}
