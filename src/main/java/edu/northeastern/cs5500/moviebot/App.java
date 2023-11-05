package edu.northeastern.cs5500.moviebot;

import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGES;
import static net.dv8tion.jda.api.requests.GatewayIntent.GUILD_MESSAGE_REACTIONS;
import static spark.Spark.*;

import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.PaginatorBuilder;
import edu.northeastern.cs5500.moviebot.commands.AddCommand;
import edu.northeastern.cs5500.moviebot.commands.HelpCommand;
import edu.northeastern.cs5500.moviebot.commands.RemoveCommand;
import edu.northeastern.cs5500.moviebot.commands.SearchCommand;
import edu.northeastern.cs5500.moviebot.commands.SubscribeCommand;
import edu.northeastern.cs5500.moviebot.commands.UnsubscribeCommand;
import edu.northeastern.cs5500.moviebot.listeners.ButtonListener;
import edu.northeastern.cs5500.moviebot.listeners.MessageListener;
import edu.northeastern.cs5500.moviebot.listeners.PrivateMessageListener;
import edu.northeastern.cs5500.moviebot.subscriptionManager.SubscriptionManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import edu.northeastern.cs5500.moviebot.commands.WatchCommand;

public class App {

    static String getBotToken() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        return processBuilder.environment().get("BOT_TOKEN");
    }

    public static String getAPIKey() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        return processBuilder.environment().get("TMDB_API_KEY");
    }

    public static void main(String[] arg) throws Exception {
        String token = getBotToken();
        if (token == null) {
            throw new IllegalArgumentException(
                    "The BOT_TOKEN environment variable is not defined.");
        }

        JDA jda =
                JDABuilder.createDefault(token, GUILD_MESSAGES, GUILD_MESSAGE_REACTIONS)
                        .setActivity(Activity.listening("Hi! I'm movieBot!"))
                        .addEventListeners(new MessageListener())
                        .addEventListeners(new PrivateMessageListener())
                        .addEventListeners(new ButtonListener())
                        .build();

        Pages.activate(PaginatorBuilder.createSimplePaginator(jda));
        
        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(new SearchCommand().getCommandConfiguration());
        commands.addCommands(new HelpCommand().getCommandConfiguration());
        commands.addCommands(new SubscribeCommand().getCommandConfiguration());
        commands.addCommands(new UnsubscribeCommand().getCommandConfiguration());
        commands.addCommands(new AddCommand().getCommandConfiguration());
        commands.addCommands(new RemoveCommand().getCommandConfiguration());
        commands.addCommands(new WatchCommand().getCommandConfiguration());

        commands.queue();

        SubscriptionManager subscriptionManager = new SubscriptionManager();
        subscriptionManager.scheduleDailyPushNotification(jda);

        port(8080);

        get(
                "/",
                (request, response) -> {
                    return "{\"status\": \"OK\"}";
                });
    }
}
