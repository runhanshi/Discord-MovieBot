package edu.northeastern.cs5500.moviebot.messageBuilder;

import info.movito.themoviedbapi.model.MovieDb;
import java.awt.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

/** This class helps to build formatted messages from MovieDb object */
public class MovieMessageBuilder {
    private static final String IMG_PATH_PREFIX = "https://image.tmdb.org/t/p/original/";

    /**
     * This method builds message from MovieDb object
     *
     * @param movie the MovieDb object to be formatted
     * @return a Message in jda format
     */
    public static Message fromMovieDb(MovieDb movie) {

        return new MessageBuilder().setEmbed(embedFromMovieDb(movie)).build();
    }

    /**
     * This method builds message embed from MovieDb object
     *
     * @param movie the MovieDb object to be embedded
     * @return a MessageEmbed in jda format
     */
    public static MessageEmbed embedFromMovieDb(MovieDb movie) {
        return new EmbedBuilder()
                .setTitle(movie.getTitle())
                .setDescription(movie.getOverview())
                .setColor(new Color(15981293))
                .setImage(IMG_PATH_PREFIX + movie.getPosterPath())
                .addField(
                        "Star",
                        movie.getCast() != null
                                ? movie.getCast().toString()
                                : "No cast information available",
                        false)
                .addField("Rating", String.valueOf(movie.getVoteAverage()), false)
                .addField("Release Date", movie.getReleaseDate(), false)
                .build();
    }
}
