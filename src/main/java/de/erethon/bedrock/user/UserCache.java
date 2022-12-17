package de.erethon.bedrock.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A simple class to load and unload user classes.
 * It registers itself as an {@link Listener} to load and unload users through join and quit events.
 *
 * @param <USER> The user object to load
 * @since 1.0.0
 * @author Fyreum
 */
public abstract class UserCache<USER extends LoadableUser> implements Listener {

    private final Plugin plugin;
    private final Map<String, UUID> nameToId;
    private final Map<UUID, USER> idToUser;
    private final Map<UUID, BukkitTask> idToTask;
    private long unloadAfter = 60*20; // seconds

    /**
     * @param plugin the plugin to register the listener with
     */
    public UserCache(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.nameToId = new HashMap<>();
        this.idToUser = new HashMap<>();
        this.idToTask = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Loads the user object for every player that is online.
     */
    public void loadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            load(player);
        }
    }

    /**
     * Loads the user object for the given player and returns the loaded user.
     *
     * @param player the player to load
     * @return the loaded user
     */
    public @NotNull USER load(@NotNull Player player) {
        USER user = getNewInstance(player);
        if (user == null) {
            throw new NullPointerException("The user instance for " + player.getName() + " is null -> getNewInstance(OfflinePlayer) has to return a NotNull instance for online players");
        }
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        nameToId.put(name, uuid);
        idToUser.put(uuid, user);
        return user;
    }

    /**
     * Loads the user object for the given offline player and returns the loaded user, or null.
     *
     * @param offlinePlayer the player to load
     * @return the loaded user, or null
     * @since 1.2.4
     */
    public @Nullable USER load(@NotNull OfflinePlayer offlinePlayer) {
        Player player = offlinePlayer.getPlayer();
        if (player != null) {
            return load(player);
        }
        USER user = getNewInstance(offlinePlayer);
        if (user == null) {
            return null;
        }
        UUID uuid = offlinePlayer.getUniqueId();
        String name = offlinePlayer.getName();

        if (name != null) {
            nameToId.put(name, uuid);
        }
        idToUser.put(uuid, user);
        scheduleUnloadTask(offlinePlayer);
        return user;
    }

    /**
     * Unloads every player that is online.
     */
    public void unloadAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unload(player);
        }
    }

    /**
     * Unloads the given player and call the {@link LoadableUser#saveUser()} method and returns the unloaded user.
     *
     * @param player the player to unload
     * @return the unloaded user, or null
     */
    public @Nullable USER unload(@NotNull OfflinePlayer player) {
        USER user = idToUser.get(player.getUniqueId());
        if (user != null) {
            user.saveUser();
        }
        nameToId.remove(player.getName());
        idToUser.remove(player.getUniqueId());
        return user;
    }

    /**
     * Clears all loaded users before loading every online player back again.
     */
    public void reloadAll() {
        unloadAll();
        loadAll();
    }

    /**
     * Calls the {@link LoadableUser#saveUser()} method for every user in the cache.
     */
    public void saveAll() {
        for (USER user : idToUser.values()) {
            user.saveUser();
        }
    }

    /**
     * Returns the cached user matching the name if found.
     * If no user is found, it will try to create a new one.
     *
     * @param name the name to get the user for
     * @return the matching user, or null
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByName(@NotNull String name) {
        UUID uuid = nameToId.get(name);
        return getByPlayer(uuid != null ? Bukkit.getOfflinePlayer(uuid) : Bukkit.getOfflinePlayer(name));
    }

    /**
     * Returns the cached user matching the name if found.
     *
     * @param name the name to get the user for
     * @return the matching user, or null
     * @since 1.2.4
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByNameIfCached(@NotNull String name) {
        UUID uuid = nameToId.get(name);
        return uuid == null ? null : getByUniqueIdIfCached(uuid);
    }

    /**
     * Returns the cached user matching the uuid if found.
     * If no user is found, it will try to create a new one.
     *
     * @param uuid the uuid to get the user for
     * @return the matching user, or null
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByUniqueId(@NotNull UUID uuid) {
        USER user = idToUser.get(uuid);
        return user != null ? user : load(Bukkit.getOfflinePlayer(uuid));
    }

    /**
     * Returns the cached user matching the uuid if found.
     *
     * @param uuid the uuid to get the user for
     * @return the matching user, or null
     * @since 1.2.4
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByUniqueIdIfCached(@NotNull UUID uuid) {
        return idToUser.get(uuid);
    }

    /**
     * Returns the cached user matching the player if found.
     * If no user is found, it will try to create a new one.
     * <br>
     * <b>Note:</b> online players should always be <b>not</b> null.
     *
     * @param player the player to get the user for
     * @return the matching user, or null
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByPlayer(@NotNull OfflinePlayer player) {
        USER user = idToUser.get(player.getUniqueId());
        return user != null ? user : load(player);
    }

    /**
     * Returns the cached user matching the player if found.
     * <br>
     * <b>Note:</b> online players should always be <b>not</b> null.
     *
     * @param player the player to get the user for
     * @return the matching user, or null
     * @since 1.2.4
     * @see UserCache#getNewInstance(OfflinePlayer)
     */
    public @Nullable USER getByPlayerIfCached(@NotNull OfflinePlayer player) {
        return idToUser.get(player.getUniqueId());
    }

    /**
     * Returns a {@link Set} of all loaded users.
     *
     * @return a Set of all loaded users
     */
    public @NotNull Set<USER> getCachedUsers() {
        return new HashSet<>(idToUser.values());
    }

    /**
     * Performs the given action for each element of the {@code Iterable}
     * until all elements have been processed or the action throws an
     * exception.
     *
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     * @since 1.2.4
     */
    public void forEach(@NotNull Consumer<USER> action) {
        idToUser.values().forEach(action);
    }

    /**
     * Returns the amount of users that are currently in the cache.
     *
     * @return the amount of users currently cached
     */
    public int getCachedUsersAmount() {
        return idToUser.size();
    }

    /**
     * Returns the duration after which an offline player gets unloaded.
     *
     * @return the duration after which an offline player gets unloaded
     * @since 1.1.0
     */
    public long getUnloadAfter() {
        return unloadAfter;
    }

    /**
     * Set the duration after which an offline player gets unloaded.
     *
     * @param unloadAfter the duration
     * @since 1.1.0
     */
    public void setUnloadAfter(long unloadAfter) {
        this.unloadAfter = unloadAfter;
    }

    /* abstracts */

    /**
     * This method tries to create a new user instance for the given player.
     * This method can return null if the given {@link OfflinePlayer} never played before or isn't online.
     * <br>
     * <b>Note:</b> online players should always be <b>not</b> null.
     *
     * @param player the player to get the user for
     * @return a new user object if possible, else null
     */
    protected abstract @Nullable USER getNewInstance(@NotNull OfflinePlayer player);

    /* listener */

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        BukkitTask kickTask = idToTask.get(uuid);

        if (kickTask != null) {
            kickTask.cancel();
            idToTask.remove(uuid);
        }
        USER user = idToUser.get(uuid);
        if (user != null) {
            user.updatePlayer(player);
            user.onJoin(event);
            return;
        }
        load(player).onJoin(event);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        USER user = getByPlayerIfCached(player);

        if (user == null) {
            return;
        }
        user.onQuit(event);

        if (unloadAfter < 0) {
            return;
        }
        if (unloadAfter == 0) {
            unload(player);
            return;
        }
        scheduleUnloadTask(player);
    }

    private void scheduleUnloadTask(OfflinePlayer player) {
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> unload(player), unloadAfter);
        idToTask.put(player.getUniqueId(), task);
    }
}
