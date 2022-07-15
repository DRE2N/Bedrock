package de.erethon.bedrock.user;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This class is the base foundation for the {@link UserCache} class.
 *
 * @since 1.0.0
 * @author Fyreum
 */
public interface LoadableUser {

    /**
     * This method should overwrite the current player.
     * Its called when a user joins to prevent that a previous loaded user has a null player.
     */
    void updatePlayer(Player player);

    /**
     * This method should save any data the user has stored.
     * Its called when a user gets unloaded.
     */
    void saveUser();

    /**
     * This method is called when the player joins.
     */
    default void onJoin(PlayerJoinEvent event) {

    }

    /**
     * This method is called when the player quits.
     */
    default void onQuit(PlayerQuitEvent event) {

    }

    /**
     * This method is called when the player gets unloaded.
     *
     * @since 1.1.0
     */
    default void onUnload() {

    }
}
