package de.erethon.bedrock.command;

import de.erethon.bedrock.config.Message;

import java.io.Serial;

/**
 * This exception is thrown when an executed command failed.
 * Causes might be unmet requirements or other thrown exceptions.
 * <br>
 * The exception message will be sent to the command sender.
 *
 * @since 1.2.2
 * @author Fyreum
 */
public class CommandFailedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4197234756319882607L;

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
