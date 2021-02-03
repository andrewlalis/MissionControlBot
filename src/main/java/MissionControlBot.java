import api.WebClientHolder;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import subscriber.CommandManager;
import subscriber.MessageHandler;
import subscriber.commands.HelpCommand;
import subscriber.commands.UpcomingLaunchesCommand;

import java.time.Duration;

/**
 * The main program entry point for the MissionControlBot application.
 */
@Slf4j
public class MissionControlBot {
	public static void main(String[] args) {
		initializeWebClient(args);
		var client = DiscordClientBuilder.create(readToken(args)).build().login().block(Duration.ofSeconds(5));
		if (client == null) {
			throw new RuntimeException("Could not obtain client and login.");
		}
		registerEventHandlers(client);
		client.onDisconnect().block();
	}

	/**
	 * @return The discord bot token from the command line args.
	 */
	private static String readToken(String[] args) {
		if (args.length < 1 || args[0].isBlank()) {
			throw new RuntimeException("Missing token argument as first argument.");
		}
		return args[0].trim();
	}

	/**
	 * Registers event handlers for the given client.
	 * @param client The client to add handlers to.
	 */
	private static void registerEventHandlers(GatewayDiscordClient client) {
		client.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(event -> {
					User self = event.getSelf();
					log.info("Logged in as {}#{}.", self.getUsername(), self.getDiscriminator());
				});
		CommandManager commandManager = new CommandManager();
		commandManager.registerCommand("help", new HelpCommand());
		commandManager.registerCommand("launches", new UpcomingLaunchesCommand());
		client.getEventDispatcher().on(new MessageHandler(commandManager)).subscribe();
	}

	/**
	 * Initializes the API client that's used to request data.
	 * @param args The command line args.
	 */
	private static void initializeWebClient(String[] args) {
		if (args.length < 2 || args[1].isBlank()) {
			throw new RuntimeException("Missing API key as second argument.");
		}
		final WebClient client = WebClient.builder()
				.defaultHeader("Authorization", "Bearer " + args[1].trim())
				.baseUrl("https://fdo.rocketlaunch.live/json")
				.build();
		WebClientHolder.set(client);
	}
}
