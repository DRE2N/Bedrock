package de.erethon.bedrock.command;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The default CommandExecutor for all ECommandCaches.
 *
 * @author Frank Baumann, Daniel Saukel, Fyreum
 */
public class ECommandExecutor implements CommandExecutor {

    protected EPlugin plugin;

    public ECommandExecutor(EPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command unused1, String unused2, String[] args) {
        ECommand command;

        if (args.length > 0) {
            command = plugin.getCommandCache().getCommand(args[0]);

            if (command != null) {
                if (sender instanceof Player player) {

                    if (!command.isPlayerCommand()) {
                        MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PLAYER_COMMAND.getMessage());
                        return false;

                    } else if (command.getPermission() != null) {
                        if (!command.senderHasPermissions(player)) {
                            MessageUtil.sendMessage(player, BedrockMessage.CMD_NO_PERMISSION.getMessage());
                            return false;
                        }
                    }
                } else {
                    if (!command.isConsoleCommand()) {
                        MessageUtil.log(BedrockMessage.CMD_NO_CONSOLE_COMMAND.getMessage());
                        return false;
                    }
                }

                if (command.getMinArgs() <= args.length - 1 & command.getMaxArgs() >= args.length - 1 || command.getMinArgs() == -1) {
                    command.execute(args, sender);
                } else {
                    command.displayHelp(sender);
                }
                return true;
            }
        }

        command = plugin.getCommandCache().getCommand("main");
        if (command != null) {
            String[] argsCopy = new String[args.length + 1];
            argsCopy[0] = "main";

            if (args.length != 0) {
                System.arraycopy(args, 0, argsCopy, 1, args.length);
            }
            command.execute(argsCopy, sender);
        } else {
            MessageUtil.sendMessage(sender, BedrockMessage.CMD_DOES_NOT_EXIST.getMessage());
        }
        return true;
    }

}
