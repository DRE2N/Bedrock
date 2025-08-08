package de.erethon.bedrock.command;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
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
        ERootCommand labelCommand = new ERootCommand(label);
        labelCommand.setExecutor(executor);
        labelCommand.setTabCompleter(this);
        boolean registered = Bukkit.getCommandMap().register(label, plugin.getName().toLowerCase(), labelCommand);
        if (!registered) {
            plugin.getLogger().severe("Failed to register label command " + label + ". It may already be registered.");
        }
        for (ECommand command : commands) {
            if (!command.isRegisterSeparately()) {
                continue;
            }
            registerCommand(plugin, command);
        }
    }

    private void registerCommand(JavaPlugin plugin, ECommand command) {
        if (command.getHelp() == null) {
            command.setDefaultHelp();
        }
        Command existingCommand = Bukkit.getCommandMap().getCommand(command.getCommand());
        if (existingCommand instanceof ERootCommand eRootCommand) {
            eRootCommand.setExecutor(command);
            eRootCommand.setTabCompleter(command);
            return;
        }
        if (existingCommand instanceof PluginCommand pluginCommand) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
            return;
        }
        registerRootCommand(plugin, command);
    }

    public void registerRootCommand(JavaPlugin plugin, ECommand eCommand) {
        List<String> aliases = new ArrayList<>(eCommand.getAliases());
        ERootCommand rootCommand = new ERootCommand(eCommand.getCommand(), eCommand.getDescription(), eCommand.getUsage(), aliases);
        if (!aliases.isEmpty()) {
            rootCommand.setAliases(aliases);
        }
        if (eCommand.getPermission() != null) {
            rootCommand.setPermission(eCommand.getPermission());
        }
        if (eCommand.getUsage() != null) {
            rootCommand.setUsage(eCommand.getUsage());
        }
        if (eCommand.getDescription() != null) {
            rootCommand.setDescription(eCommand.getDescription());
        }
        rootCommand.setPermission(eCommand.getPermission());
        rootCommand.setTabCompleter(eCommand);
        rootCommand.setExecutor(eCommand);

        boolean registered = Bukkit.getCommandMap().register(label, plugin.getName().toLowerCase(), rootCommand);
        if (!registered) {
            plugin.getLogger().severe("Failed to register command " + eCommand.getCommand() + ". It may already be registered.");
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
