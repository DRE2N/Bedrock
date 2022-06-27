package de.erethon.bedrock.command;

import de.erethon.bedrock.config.Message;

/**
 * This exception is thrown when an executed command failed.
 * Causes might be unmet requirements or other thrown exceptions.
 * <br>
 * The exception message will be sent to the command sender.
 *
 * @author Fyreum
 */
public class CommandFailedException extends RuntimeException {

    public CommandFailedException() {
        super();
    }

    public CommandFailedException(Message message) {
        super(message.getMessage());
    }

    public CommandFailedException(Message message, String... args) {
        super(message.getMessage(args));
    }

    public CommandFailedException(String message) {
        super(message);
    }
}
