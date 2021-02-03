package subscriber;

import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import util.AppProperties;

import java.util.Arrays;

/**
 * Main consumer that processes incoming messages.
 */
@Slf4j
public class MessageHandler extends ReactiveEventAdapter {
	private final CommandManager commandManager;

	public MessageHandler(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	@Override
	public Publisher<?> onMessageCreate(MessageCreateEvent event) {
		if (!event.getMessage().getContent().startsWith(AppProperties.getInstance().get("commands.prefix"))) {
			return Mono.empty(); // The message doesn't start with our prefix, so ignore it.
		}
		final String[] words = event.getMessage().getContent().split(" ");
		if (words.length < 2) {
			return this.commandManager.handle(event, "help", new String[0]);
		}
		return this.commandManager.handle(event, words[1], Arrays.copyOfRange(words, 2, words.length));
	}
}
