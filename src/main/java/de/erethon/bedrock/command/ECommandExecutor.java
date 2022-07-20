package de.erethon.bedrock.command;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * The default CommandExecutor for all ECommandCaches.
 *
 * @since 1.0.0
 * @author Frank Baumann, Daniel Saukel, Fyreum
 */
public class ECommandExecutor implements CommandExecutor {

    protected EPlugin plugin;

    public ECommandExecutor(EPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String unused2, String[] args) {
        ECommand cmd;

        if (args.length > 0) {
            cmd = plugin.getCommandCache().getCommand(args[0]);

            if (cmd != null) {
                return cmd.onCommand(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
            }
        }
        cmd = plugin.getCommandCache().getCommand("main");

        if (cmd != null) {
            String[] argsCopy = new String[args.length + 1];
            argsCopy[0] = "main";

            if (args.length != 0) {
                System.arraycopy(args, 0, argsCopy, 1, args.length);
            }
            cmd.execute(argsCopy, sender);
        } else {
            MessageUtil.sendMessage(sender, BedrockMessage.CMD_DOES_NOT_EXIST.getMessage());
        }
        return true;
    }

}
