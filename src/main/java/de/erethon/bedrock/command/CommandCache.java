package de.erethon.bedrock.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @since 1.0.0
 * @author Sataniel, Fyreum
 */
public class CommandCache implements Iterable<ECommand> {

    protected final Set<ECommand> commands;

    public CommandCache(Set<ECommand> commands) {
        this.commands = commands;
    }

    public CommandCache(ECommand... commands) {
        this.commands = new HashSet<>(Arrays.asList(commands));
    }

    /**
     * @param commandName usually the first command argument
     * @return the command with the given name
     */
    public ECommand getCommand(String commandName) {
        for (ECommand command : commands) {
            if (command.getCommand().equalsIgnoreCase(commandName) || command.getAliases().contains(commandName)) {
                return command;
            }
        }
        return null;
    }

    /**
     * @return the commands
     */
    public Set<ECommand> getCommands() {
        return this.commands;
    }

    /**
     * @param commands the commands to add
     */
    public void addCommands(ECommand... commands) {
        for (ECommand command : commands) {
            addCommand(command);
        }
    }

    /**
     * @param command the command to add
     */
    public void addCommand(ECommand command) {
        this.commands.add(command);
    }

    /**
     * @param command the command to remove
     */
    public void removeCommand(ECommand command) {
        this.commands.remove(command);
    }

    @Override
    public Iterator<ECommand> iterator() {
        return commands.iterator();
    }

    @Override
    public void forEach(Consumer<? super ECommand> action) {
        commands.forEach(action);
    }

    @Override
    public Spliterator<ECommand> spliterator() {
        return commands.spliterator();
    }
}
