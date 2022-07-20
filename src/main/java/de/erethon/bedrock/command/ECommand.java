package de.erethon.bedrock.command;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.config.Message;
import de.erethon.bedrock.misc.InfoUtil;
import de.erethon.bedrock.misc.JavaUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public abstract class ECommand implements CommandExecutor, TabCompleter {

    private String command;
    private final Set<String> aliases = new HashSet<>();
    private int minArgs;
    private int maxArgs;
    private String description;
    private String usage;
    private HelpType helpType = HelpType.DEFAULT;
    private String listedHelpHeader;
    private String paginatedHelpHeader;
    private String help;
    private String permission;
    private boolean playerCommand;
    private boolean consoleCommand;
    private boolean registerSeparately;
    private final CommandCache subCommands = new CommandCache();
    private String executionPrefix = "";

    public void displayHelp(CommandSender sender) {
        switch (helpType) {
            case LISTED -> {
                if (listedHelpHeader == null) {
                    InfoUtil.sendListedHelp(sender, subCommands);
                } else {
                    InfoUtil.sendListedHelp(sender, subCommands, listedHelpHeader);
                }
            }
            case PAGINATED -> {
                if (paginatedHelpHeader == null) {
                    InfoUtil.sendPaginatedHelp(sender, subCommands, false);
                } else {
                    InfoUtil.sendPaginatedHelp(sender, subCommands, paginatedHelpHeader, false);
                }
            }
            default -> MessageUtil.sendMessage(sender, "<red>" + getHelp());
        }
    }

    public void addSubCommand(ECommand command) {
        subCommands.addCommand(command);
    }

    public void addSubCommands(ECommand... commands) {
        subCommands.addCommands(commands);
    }

    /**
     * @param arg the arg to check
     *
     * @return true if the name or one alias matches the arg
     */
    public boolean matches(String arg) {
        return arg.equalsIgnoreCase(command) | aliases.contains(arg);
    }

    /**
     * @param sender the sender to check
     *
     * @return if the sender has permission to use the command
     */
    public boolean senderHasPermissions(CommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    /* command logic */

    /**
     * @since 1.2.4
     */
    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!isPlayerCommand()) {
                MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PLAYER_COMMAND.getMessage());
                return true;
            }
            if (!senderHasPermissions(player)) {
                MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PERMISSION.getMessage());
                return true;
            }
        } else {
            if (!isConsoleCommand()) {
                MessageUtil.log(BedrockMessage.CMD_NO_CONSOLE_COMMAND.getMessage());
                return true;
            }
        }
        if (args.length < getMinArgs() | args.length > getMaxArgs()) {
            displayHelp(sender);
            return true;
        }
        execute(JavaUtil.addBeforeArray(args, label), sender);
        return true;
    }

    /**
     * @since 1.2.4
     */
    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return tabComplete(sender, JavaUtil.addBeforeArray(args, alias));
    }

    /**
     * Returns a list of strings to tab complete including all sub commands of this class.
     *
     * @param sender the command sender
     * @param args the given args
     *
     * @return a list of strings to tab complete including all sub commands
     */
    public final List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> completes = new ArrayList<>();
        List<String> commandCompletes = onTabComplete(sender, args);
        String cmd = args[1];

        if (commandCompletes != null) {
            completes.addAll(commandCompletes);
        }

        if(args.length == 2) {
            List<String> cmds = new ArrayList<>();
            for (ECommand command : subCommands.getCommands()) {
                if (command.senderHasPermissions(sender)) {
                    cmds.add(command.getCommand());
                }
            }
            for(String string : cmds) {
                if(string.toLowerCase().startsWith(cmd.toLowerCase())) {
                    completes.add(string);
                }
            }
            return completes;
        }
        for (ECommand command : subCommands.getCommands()) {
            if (command.matches(cmd)) {
                completes.addAll(command.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length)));
            }
        }
        return completes;
    }

    /**
     * Returns a list of strings to tab complete.
     * The returned value can be null.
     * This method will most likely be overridden by the certain command.
     *
     * @param sender the command sender
     * @param args the given args
     *
     * @return a list of strings to tab complete
     */
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }

    public final void execute(String[] args, CommandSender sender) {
        if (args.length != 0) {
            String[] argsCopy = Arrays.copyOfRange(args, 1, args.length);

            if (argsCopy.length > 0) {
                ECommand command = this.subCommands.getCommand(argsCopy[0]);

                if (command != null) {
                    if (sender instanceof Player player) {
                        if (!command.isPlayerCommand()) {
                            MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PLAYER_COMMAND.getMessage());
                            return;
                        }
                        if (!command.senderHasPermissions(player)) {
                            MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PERMISSION.getMessage());
                            return;
                        }
                    } else {
                        if (!command.isConsoleCommand()) {
                            MessageUtil.log(BedrockMessage.CMD_NO_CONSOLE_COMMAND.getMessage());
                            return;
                        }
                    }
                    if (!(command.getMinArgs() <= argsCopy.length - 1 & command.getMaxArgs() >= argsCopy.length - 1) && command.getMinArgs() != -1) {
                        command.displayHelp(sender);
                        return;
                    }
                    command.execute(argsCopy, sender);
                    return;
                }
            }
        }
        try {
            onExecute(args, sender);
        } catch (CommandFailedException e) {
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                displayHelp(sender);
                return;
            }
            MessageUtil.sendMessage(sender, message);
        }
    }

    /* assertion */

    /**
     * @since 1.2.2
     */
    @Contract("false -> fail")
    protected void assure(boolean b) {
        assure(b, getHelp());
    }

    /**
     * @since 1.2.2
     */
    @Contract("false, _ -> fail")
    protected void assure(boolean b, Message message) {
        assure(b, message.getMessage());
    }

    /**
     * @since 1.2.2
     */
    @Contract("false, _, _ -> fail")
    protected void assure(boolean b, @NotNull Message msg, @NotNull String... args) {
        assure(b, msg.getMessage(args));
    }

    /**
     * @since 1.2.2
     */
    @Contract("false, _ -> fail")
    protected void assure(boolean b, String msg) {
        if (!b) {
            throw new CommandFailedException(msg);
        }
    }

    /* getter and setter */

    protected String getFinalArg(String[] args, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            sb.append(args[i]);
            if (i != args.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    /**
     * @return the command name
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command name to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * @return the command aliases
     */
    public Set<String> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the command aliases to set
     */
    public void setAliases(String... aliases) {
        this.aliases.addAll(Arrays.asList(aliases));
    }

    /**
     * @return the minimal amount of arguments
     */
    public int getMinArgs() {
        return minArgs;
    }

    /**
     * @param minArgs the minimal amount of arguments to set
     */
    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    /**
     * @return the maximum amount of arguments
     */
    public int getMaxArgs() {
        return maxArgs;
    }

    /**
     * @param maxArgs the maximum amount of arguments to set
     */
    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    /**
     * @return the command the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the command description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the command usage
     */
    public String getUsage() {
        return usage;
    }

    /**
     * @return the colored command usage
     */
    public String getColoredUsage() {
        return colorUsage(usage);
    }

    /**
     * @param usage the command usage to set
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * @return the help message
     */
    public String getHelp() {
        return help;
    }

    /**
     * @param help the help message to set
     */
    public void setHelp(String help) {
        this.help = help;
    }

    /**
     * Sets the default formatted help.
     */
    public void setDefaultHelp() {
        setHelp(formatDefaultHelp(usage, description));
    }

    /**
     * @return the help type
     */
    public HelpType getHelpType() {
        return helpType;
    }

    /**
     * @param type the help type to set
     */
    public void setHelpType(HelpType type) {
        this.helpType = type;
    }

    /**
     * @return the listed help header
     */
    public String getListedHelpHeader() {
        return listedHelpHeader;
    }

    /**
     * @param listedHelpHeader the listed help header to set
     */
    public void setListedHelpHeader(String listedHelpHeader) {
        this.listedHelpHeader = listedHelpHeader;
    }

    /**
     * @return the paginated help header
     */
    public String getPaginatedHelpHeader() {
        return paginatedHelpHeader;
    }

    /**
     * @param paginatedHelpHeader the paginated help header to set
     */
    public void setPaginatedHelpHeader(String paginatedHelpHeader) {
        this.paginatedHelpHeader = paginatedHelpHeader;
    }

    /**
     * @return the permission to use the command
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to use the command to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @return if a player may use the command
     */
    public boolean isPlayerCommand() {
        return playerCommand;
    }

    /**
     * @param playerCommand set if a player may use the command
     */
    public void setPlayerCommand(boolean playerCommand) {
        this.playerCommand = playerCommand;
    }

    /**
     * @return if the console may use the command
     */
    public boolean isConsoleCommand() {
        return consoleCommand;
    }

    /**
     * @param consoleCommand set if the console may use the command
     */
    public void setConsoleCommand(boolean consoleCommand) {
        this.consoleCommand = consoleCommand;
    }

    /**
     * @return if this command should be registered separately
     * @since 1.2.4
     */
    public boolean isRegisterSeparately() {
        return registerSeparately;
    }

    /**
     * @param registerSeparately set if this command should be registered separately
     * @since 1.2.4
     */
    public void setRegisterSeparately(boolean registerSeparately) {
        this.registerSeparately = registerSeparately;
    }

    /**
     * @return the sub command cache
     */
    public CommandCache getSubCommands() {
        return subCommands;
    }

    /**
     * @return the execution prefix
     */
    public String getExecutionPrefix() {
        return executionPrefix;
    }

    /**
     * @param executionPrefix the execution prefix
     */
    public void setExecutionPrefix(String executionPrefix) {
        this.executionPrefix = executionPrefix;
    }

    /**
     * Sets the execution prefix for every sub command and its sub commands etc.
     */
    public void setAllExecutionPrefixes() {
        for (ECommand sub : getSubCommands()) {
            sub.setExecutionPrefix(getExecutionPrefix() + getCommand() + " ");
            sub.setAllExecutionPrefixes();
        }
    }

    /* Abstracts */
    /**
     * @param args   the arguments to pass from the command
     * @param sender the player or console that sent the command
     */
    public abstract void onExecute(String[] args, CommandSender sender);

    /* Statics */

    /**
     * Colors the usage via the default scheme.
     *
     * @param usage the command usage to color
     * @return the colored usage
     */
    public static String colorUsage(String usage) {
        if (usage == null) {
            return "MISSING_USAGE";
        }
        return "&6" + MessageUtil.stripColor(usage).replace("[", "[&e").replace("|", "&6|&e").replace("]", "&6]");
    }

    /**
     * Formats the usage and description via the default scheme.
     *
     * @param usage the command usage to format
     * @param description the command description to format
     * @return the formatted default help
     */
    public static String formatDefaultHelp(String usage, String description) {
        return description != null ? colorUsage(usage) + " &8- &7" + description : colorUsage(usage);
    }

    /**
     * Defines the method to send help messages with.
     */
    public enum HelpType {
        DEFAULT,
        LISTED,
        PAGINATED
    }
}
