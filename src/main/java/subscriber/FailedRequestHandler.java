package subscriber;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

/**
 * Simple method for displaying a message about why some request failed.
 * @param <T> The type that would normally be passed on in the reactive chain.
 */
public class FailedRequestHandler<T> implements Function<Throwable, Mono<?>> {
	private final MessageCreateEvent event;

	public FailedRequestHandler(MessageCreateEvent event) {
		this.event = event;
	}

	@Override
	public Mono<T> apply(Throwable throwable) {
		return event.getMessage().getChannel()
				.flatMap(channel -> channel.createMessage(spec -> {
					spec.setMessageReference(event.getMessage().getId());
					spec.setContent("Request failed: " + throwable.getMessage());
				}))
				.delayElement(Duration.ofSeconds(3))
				.flatMap(message -> Mono.zip(message.delete(), event.getMessage().delete()))
				.then(Mono.empty());
	}
}
