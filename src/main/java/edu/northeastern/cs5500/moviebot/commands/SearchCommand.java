package edu.northeastern.cs5500.moviebot.commands;

import edu.northeastern.cs5500.moviebot.App;
import edu.northeastern.cs5500.moviebot.listeners.ButtonListener;
import edu.northeastern.cs5500.moviebot.messageBuilder.MovieMessageBuilder;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbFind;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.FindResults;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;

public class SearchCommand implements Command {

    /**
     * This method creates the command configuration for search command. It sends configuration to
     * JDA to set up search command for the movie bot.
     *
     * @return CommandData in JDA format
     */
    @Override
    public CommandData getCommandConfiguration() {
        return new CommandData(
                        "search", "make the bot echo back the the ID/title/name provided for now")
                .addSubcommands(
                        new SubcommandData("id", "search movie by movie id")
                                .addOptions(
                                        new OptionData(
                                                        OptionType.STRING,
                                                        "movie_id",
                                                        "type the movie id")
                                                .setRequired(true)))
                .addSubcommands(
                        new SubcommandData("title", "search movie by movie title")
                                .addOptions(
                                        new OptionData(
                                                        OptionType.STRING,
                                                        "movie_title",
                                                        "type the movie title")
                                                .setRequired(true)));
    }

    /**
     * This method handles commands from user.
     *
     * @param event consists of user input
     */
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        TmdbApi api = new TmdbApi(App.getAPIKey());
        switch (event.getSubcommandName()) {
            case "id":
                TmdbFind find = api.getFind();
                FindResults idResults =
                        find.find(
                                event.getOption("movie_id").getAsString(),
                                TmdbFind.ExternalSource.imdb_id,
                                "en_US");
                if (idResults.getMovieResults().size() == 0) {
                    String messageInvalid =
                            "This ID "
                                    + event.getOption("movie_id").getAsString()
                                    + " is not found. Please try another ID.";
                    event.reply(messageInvalid).queue();
                } else {
                    event.reply(MovieMessageBuilder.fromMovieDb(idResults.getMovieResults().get(0)))
                            .queue();
                }
                break;
            case "title":
                TmdbSearch search = api.getSearch();
                MovieResultsPage titleResults =
                        search.searchMovie(
                                event.getOption("movie_title").getAsString(), 0, "en_US", true, 1);

                List<MovieDb> movieList = titleResults.getResults();
                if (movieList.size() == 0) {
                    event.reply("invalid movie title. Please try another one. ").queue();
                } else {
                    ButtonListener listener =
                            (ButtonListener) event.getJDA().getRegisteredListeners().get(2);
                    listener.addCommunication(event.getChannel().getId(), movieList);
                    event.reply(MovieMessageBuilder.fromMovieDb(movieList.get(0)))
                            .addActionRow(
                                    Button.primary("prev", "prev"), Button.success("next", "next"))
                            .queue();
                }
                break;
        }
    }

    @Override
    public String getName() {
        return "search";
    }
}
