package de.erethon.bedrock.player;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Daniel Saukel
 */
public interface PlayerWrapper {

    /**
     * Returns the Bukkit Player object.
     *
     * @return the Bukkit Player object
     */
    Player getPlayer();

    /**
     * Returns the player's name
     *
     * @return the player's name
     */
    String getName();

    /**
     * Returns the player's unique ID
     *
     * @return the player's unique ID
     */
    UUID getUniqueId();

}
