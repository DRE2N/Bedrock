package de.erethon.bedrock.player;

import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * @since 1.0.0
 * @author Daniel Saukel
 */
public class PlayerUtil {

    /**
     * Returns the unique ID of the player that has the name
     *
     * @param name a player's name
     * @return the player's UUID
     */
    public static UUID getUniqueIdFromName(String name) {
        return Bukkit.getServer().getOfflinePlayer(name).getUniqueId();
    }

    /**
     * Returns the name of the player that has the unique ID
     *
     * @param uuid the player's UUID as a String
     * @return the player's name
     */
    public static String getNameFromUniqueId(String uuid) {
        return Bukkit.getServer().getOfflinePlayer(UUID.fromString(uuid)).getName();
    }

    /**
     * Returns if the String can be converted to a UUID
     *
     * @param string a UUID as a String
     * @return if the String can be converted to a UUID
     */
    public static boolean isValidUUID(String string) {
        return string.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

}
