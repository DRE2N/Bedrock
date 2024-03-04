package de.erethon.bedrock.misc;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.command.CommandCache;
import de.erethon.bedrock.command.ECommand;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.plugin.EPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @since 1.2.1
 * @author Fyreum
 */
public class InfoUtil {

    private static final Component COMMA = Component.text("&c,");
    private static final Component SEPARATOR = COMMA.append(Component.text(" "));

    public static void sendPaginatedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache, boolean commandPrefix) {
        sendPaginatedHelp(sender, commandCache, BedrockMessage.INFO_COMMANDS.getMessage(), commandPrefix);
    }

    public static void sendPaginatedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache, @NotNull String[] args,
                                         boolean commandPrefix) {
        sendPaginatedHelp(sender, commandCache, BedrockMessage.INFO_COMMANDS.getMessage(), args, commandPrefix);
    }

    public static void sendPaginatedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache, @NotNull String header,
                                         boolean commandPrefix) {
        sendPaginatedHelp(sender, commandCache, header, new String[0], commandPrefix);
    }

    public static void sendPaginatedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache, @NotNull String header,
                                         @NotNull String[] args, boolean commandPrefix) {
        Set<ECommand> dCommandSet = commandCache.getCommands();
        List<ECommand> sorted = dCommandSet.stream()
                .filter(command -> command.senderHasPermissions(sender))
                .sorted(Comparator.comparing(ECommand::getCommand))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            MessageUtil.sendMessage(sender, BedrockMessage.CMD_NO_PERMISSION.getMessage());
            return;
        }

        int page = args.length == 2 ? NumberUtil.parseInt(args[1], 1) : 1;
        int perPage = EPlugin.getInstance().getBedrockConfig().getCommandsPerHelpPage();

        sendPaginatedInfo(sender, sorted, header, eCommand -> {
            String prefix = commandPrefix ? "&2" + eCommand.getCommand() + "&8 - &7" : "";
            return MessageUtil.parse(prefix + eCommand.getHelp());
        }, page, perPage);
    }

    /**
     * @since 1.2.4
     */
    public static <T> void sendPaginatedInfo(@NotNull CommandSender sender, @NotNull Collection<T> information, @NotNull String headerName,
                                             @NotNull ComponentConverter<T> converter) {
        sendPaginatedInfo(sender, information, headerName, converter, 1);
    }

    /**
     * @since 1.2.4
     */
    public static <T> void sendPaginatedInfo(@NotNull CommandSender sender, @NotNull Collection<T> information, @NotNull String headerName,
                                             @NotNull ComponentConverter<T> converter, int page) {
        sendPaginatedInfo(sender, information, headerName, converter, page,
                EPlugin.getInstance().getBedrockConfig().getInformationPerPage());
    }

    /**
     * @since 1.2.4
     */
    public static <T> void sendPaginatedInfo(@NotNull CommandSender sender, @NotNull Collection<T> information, @NotNull String headerName,
                                             @NotNull ComponentConverter<T> converter, int page, int perPage) {
        ArrayList<T> toSend = new ArrayList<>();

        int send = 0;
        int max = 0;
        int min = 0;

        for (T info : information) {
            send++;
            if (send >= page * perPage - (perPage - 1) && send <= page * perPage) {
                min = page * perPage - (perPage - 1);
                max = page * perPage;
                toSend.add(info);
            }
        }

        MessageUtil.sendCenteredMessage(sender, "&4&l[&r &6" + headerName + " &4&l]");
        MessageUtil.sendCenteredMessage(sender, "&4&l[&r &6" + min + "-" + max + " &4/&6 " + send + " &4|&6 " + page + " &4&l]");

        for (T info : toSend) {
            MessageUtil.sendMessage(sender, MessageUtil.serialize(converter.convert(info)));
        }
    }

    public static void sendListedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache) {
        sendListedHelp(sender, commandCache, BedrockMessage.INFO_COMMANDS.getMessage());
    }

    public static void sendListedHelp(@NotNull CommandSender sender, @NotNull CommandCache commandCache, @NotNull String header) {
        Set<ECommand> dCommandSet = commandCache.getCommands();
        List<ECommand> sorted = dCommandSet.stream()
                .filter(command -> command.senderHasPermissions(sender))
                .sorted(Comparator.comparing(ECommand::getCommand))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) {
            sender.sendMessage(BedrockMessage.CMD_NO_PERMISSION.getMessage());
            return;
        }
        sendListedInfo(sender, sorted, header, ECommand::asComponent);
    }

    public static String toString(CommandCache commands) {
        int size = commands.getCommands().size();
        if (size == 0) {
            return BedrockMessage.HOVER_NONE.getMessage();
        }
        if (size == 1) {
            ECommand first = commands.getCommands().stream().findFirst().orElse(null);
            return first == null ? BedrockMessage.HOVER_NONE.getMessage() : first.getCommand(); // shouldn't be null
        }
        List<String> names = new ArrayList<>(size);
        for (ECommand command : commands) {
            names.add(command.getCommand());
        }
        return JavaUtil.toString(names);
    }

    public static <T> void sendListedInfo(@NotNull CommandSender sender, @NotNull Collection<T> information, @NotNull String headerName,
                                          @NotNull ComponentConverter<T> converter) {
        MessageUtil.sendCenteredMessage(sender, BedrockMessage.INFO_HEADER.getMessage(String.valueOf(information.size()), headerName));
        sendListedInfo(sender, information, converter);
    }

    public static <T> void sendListedInfo(@NotNull CommandSender sender, @NotNull Collection<T> information, @NotNull ComponentConverter<T> converter) {
        List<Component> messages = new ArrayList<>();
        int charLimit = Integer.MAX_VALUE - 1;
        long charAmount = 1;
        Component message = Component.space();

        for (T info : information) {
            Component component = converter.convert(info);
            int length = charAmount == 0 ? getLength(component) : getLength(component) + 2; // length + separator

            if (charAmount + length > charLimit) {
                messages.add(message.append(COMMA));
                message = Component.space();
                charAmount = 1;
            }
            if (charAmount != 1) {
                message = message.append(SEPARATOR);
            }
            message = message.append(component);
            charAmount += length;
        }
        messages.add(message);

        for (Component msg : messages) {
            MessageUtil.sendMessage(sender, MessageUtil.serialize(msg));
        }
    }

    private static int getLength(Component component) {
        return ChatColor.stripColor(MessageUtil.stripTokens(MessageUtil.serialize(component))).length();
    }

}
