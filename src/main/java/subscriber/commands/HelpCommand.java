package subscriber.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;
import org.reactivestreams.Publisher;
import subscriber.Command;
import util.AppProperties;

import java.time.Duration;

/**
 * This command displays some nicely formatted help information about the
 * various commands which one can use.
 */
public class HelpCommand implements Command {
	@Override
	public Publisher<?> handle(MessageCreateEvent event, String[] args) {
		final String prefix = AppProperties.getInstance().get("commands.prefix") + ' ';
		return event.getMessage().getChannel().flatMap(channel -> channel.createEmbed(spec -> {
			spec.setColor(Color.CINNABAR);
			spec.setTitle("Mission Control - Help");
			spec.addField(prefix + "help", "Show this message.", false);
			spec.addField(prefix + "launches", "Show the next few upcoming launches.", false);
		}))
		.delayElement(Duration.ofSeconds(1))
		.flatMap(msg -> event.getMessage().delete());
	}
}
