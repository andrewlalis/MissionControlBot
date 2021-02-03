package subscriber.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import subscriber.Command;

import java.time.Duration;

public class InvalidCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getMessage().getChannel()
				.flatMap(channel -> channel.createMessage(spec -> {
					spec.setMessageReference(event.getMessage().getId());
					spec.setContent("Invalid command.");
				}))
				.delayElement(Duration.ofSeconds(3))
				.flatMap(message -> Mono.zip(message.delete(), event.getMessage().delete()));
	}
}
