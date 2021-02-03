package subscriber;

import discord4j.core.event.domain.message.MessageCreateEvent;
import org.reactivestreams.Publisher;
import subscriber.commands.InvalidCommand;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the list of registered commands and handles delegation of execution to
 * a particular command implementation.
 */
public class CommandManager {
	private final Map<String, Command> commandMap;

	public CommandManager() {
		this.commandMap = new ConcurrentHashMap<>();
	}

	public void registerCommand(String commandKeyword, Command command) {
		if (this.commandMap.containsKey(commandKeyword)) {
			throw new IllegalArgumentException("A command is already registered for the keyword " + commandKeyword);
		}
		this.commandMap.put(commandKeyword, command);
	}

	public Publisher<?> handle(MessageCreateEvent event, String commandKeyword, String[] args) {
		Command command = this.commandMap.getOrDefault(commandKeyword, new InvalidCommand());
		return command.handle(event, args);
	}
}
