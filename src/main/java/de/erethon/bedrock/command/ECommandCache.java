package de.erethon.bedrock.command;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Note that Erethon plugins are usually designed to have just one instance of ECommandCache.
 * One instance of ECommandCache represents one command and contains all of its subcommands.
 *
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class ECommandCache extends CommandCache implements TabCompleter {

    private final String label;
    private final CommandExecutor executor;
    private boolean tabCompletion = true;

    public ECommandCache(String label, EPlugin plugin, Set<ECommand> commands) {
        super(commands);
        this.label = label;
        this.executor = new ECommandExecutor(plugin);
    }

    public ECommandCache(String label, EPlugin plugin, ECommand... commands) {
        super(commands);
        this.label = label;
        this.executor = new ECommandExecutor(plugin);
    }

    public ECommandCache(String label, CommandExecutor executor, Set<ECommand> commands) {
        super(commands);
        this.label = label;
        this.executor = executor;
    }

    public ECommandCache(String label, CommandExecutor executor, ECommand... commands) {
        super(commands);
        this.label = label;
        this.executor = executor;
    }

    /**
     * @return the command label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return true if TabCompletion is enabled, false otherwise
     */
    public boolean isTabCompletion() {
        return tabCompletion;
    }

    /**
     * @param tabCompletion the boolean to set
     */
    public void setTabCompletion(boolean tabCompletion) {
        this.tabCompletion = tabCompletion;
    }

    /**
     * @param plugin the plugin that registers the command.
     */
    public void register(JavaPlugin plugin) {
        for (ECommand command : commands) {
            if (!command.isRegisterSeparately()) {
                continue;
            }
            registerCommand(plugin, command);
        }
        Constructor<PluginCommand> pluginCommand;
        try {
            pluginCommand = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        pluginCommand.setAccessible(true);
        PluginCommand pluginCommandInstance;
        try {
            pluginCommandInstance = pluginCommand.newInstance(label, plugin);
        } catch (Exception e) {
            MessageUtil.log("Couldn't register command '" + label + "' cause: " + e.getMessage());
            return;
        }
        pluginCommandInstance.setExecutor(executor);
        if (tabCompletion) {
            pluginCommandInstance.setTabCompleter(this);
        }
    }

    private void registerCommand(JavaPlugin plugin, ECommand command) {
        if (command.getHelp() == null) {
            command.setDefaultHelp();
        }
        registerNewCommand(plugin, command);
    }

    private void registerNewCommand(JavaPlugin plugin, ECommand command) {
        try {
            final Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            final PluginCommand bukkitCommand = c.newInstance(command.getCommand(), plugin);
            if (command.getAliases() != null) {
                bukkitCommand.setAliases(new ArrayList<>(command.getAliases()));
            }
            if (command.getDescription() != null) {
                bukkitCommand.setDescription(command.getDescription());
            }
            if (command.getUsage() != null) {
                bukkitCommand.setUsage(command.getUsage());
            }
            bukkitCommand.setPermission(command.getPermission());
            bukkitCommand.permissionMessage(BedrockMessage.CMD_NO_PERMISSION.message());
            bukkitCommand.setTabCompleter(command);
            bukkitCommand.setExecutor(command);

            Bukkit.getCommandMap().register(label, bukkitCommand);
        } catch (Exception e) {
            MessageUtil.log("Couldn't register command '" + command.getCommand() + "' cause: " + e.getMessage());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command unused1, String unused2, String[] args) {
        List<String> completes = new ArrayList<>();
        String cmd = args[0];

        if(args.length == 1) {
            List<String> cmds = new ArrayList<>();
            for (ECommand command : commands) {
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
        for (ECommand command : commands) {
            if (command.matches(cmd)) {
                completes.addAll(command.tabComplete(sender, args));
            }
        }
        return completes;
    }
}
