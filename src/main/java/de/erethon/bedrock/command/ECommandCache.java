package de.erethon.bedrock.command;

import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Note that Erethon plugins are usually designed to have just one instance of ECommandCache.
 * One instance of ECommandCache represents one command and contains all of its subcommands.
 *
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
        plugin.getCommand(label).setExecutor(executor);
        if (tabCompletion) {
            plugin.getCommand(label).setTabCompleter(this);
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
