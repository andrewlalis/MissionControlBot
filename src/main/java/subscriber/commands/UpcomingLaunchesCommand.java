package subscriber.commands;

import api.LaunchResponse;
import api.WebClientHolder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import subscriber.Command;
import subscriber.FailedRequestHandler;

import java.time.Duration;
import java.time.Instant;

/**
 * This command displays information about a few upcoming launches.
 */
@Slf4j
public class UpcomingLaunchesCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		return event.getMessage().addReaction(ReactionEmoji.unicode("\uD83D\uDE80"))
			.then(this.doRequest()
				.onErrorResume(throwable -> new FailedRequestHandler<LaunchResponse.Paged>(event).apply(throwable))
				.flatMap(paged -> event.getMessage().getChannel()
					.flatMap(channel -> this.createEmbed(channel, paged)))
				.delayElement(Duration.ofSeconds(1))
				.flatMap(msg -> event.getMessage().delete())
			);
	}

	/**
	 * @return The mono representing the HTTP request operation and immediate
	 * response preprocessing.
	 */
	private Mono<LaunchResponse.Paged> doRequest() {
		return WebClientHolder.getClient().get()
				.uri("/launches?limit=3")
				.retrieve()
				.onStatus(HttpStatus::isError, response -> {
					log.error("Error response from the API:\n{}", response.toString());
					return Mono.error(new IllegalStateException(response.statusCode().toString()));
				})
				.bodyToMono(LaunchResponse.Paged.class);
	}

	/**
	 * Creates the actual embed message for a launch response.
	 * @param channel The channel to create the message in.
	 * @param response The response received from the API.
	 * @return A mono linked to the message creation.
	 */
	private Mono<Message> createEmbed(MessageChannel channel, LaunchResponse.Paged response) {
		return channel.createEmbed(spec -> {
			spec.setFooter("Data by RocketLaunch.Live", "https://www.rocketlaunch.live/res/favicon32.png");
			spec.setTimestamp(Instant.now());
			spec.setColor(Color.ENDEAVOUR);
			spec.setTitle("Upcoming Launches");
			spec.setThumbnail("https://raw.githubusercontent.com/andrewlalis/MissionControlBot/main/design/icon.png");
			response.getResult().forEach(launchResponse -> {
				spec.addField(launchResponse.getName(), launchResponse.getLaunchDescription(), false);
			});
		});
	}
}
