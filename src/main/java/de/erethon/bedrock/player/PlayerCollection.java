package de.erethon.bedrock.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class PlayerCollection implements Iterable<UUID> {

    private final Collection<UUID> uuids = new HashSet<>();

    /**
     * Creates an empty PlayerCollection
     */
    public PlayerCollection() {
    }

    /**
     * @param players a collection of Player, OfflinePlayer, UUID, String (player names), String (uuids) and PlayerWrapper objects
     */
    public PlayerCollection(@NotNull Collection<?> players) {
        addAll(players);
    }

    /**
     * @return a collection of UUIDs
     */
    public @NotNull Collection<UUID> getUniqueIds() {
        return new ArrayList<>(uuids);
    }

    /**
     * @param filter players to exclude
     * @return a collection of UUIDs
     */
    public @NotNull Collection<UUID> getUniqueIds(PlayerCollection filter) {
        Collection<UUID> filtered = new ArrayList<>();
        for (UUID uuid : uuids) {
            if (!filter.contains(uuid)) {
                filtered.add(uuid);
            }
        }
        return filtered;
    }

    /**
     * @return a collection of player name Strings
     */
    public @NotNull Collection<String> getNames() {
        Collection<String> filtered = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids) {
            filtered.add(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return filtered;
    }

    /**
     * @param filter players to exclude
     * @return a collection of player name Strings
     */
    public @NotNull Collection<String> getNames(@NotNull PlayerCollection filter) {
        Collection<String> filtered = new ArrayList<>();
        for (UUID uuid : uuids) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            if (!filter.contains(uuid)) {
                filtered.add(name);
            }
        }
        return filtered;
    }

    /**
     * @return a collection of OnlinePlayers
     */
    public @NotNull Collection<Player> getOnlinePlayers() {
        Collection<Player> filtered = new ArrayList<>();
        for (UUID uuid : uuids) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                filtered.add(player);
            }
        }
        return filtered;
    }

    /**
     * @param filter players to exclude
     * @return a collection of OnlinePlayers
     */
    public @NotNull Collection<Player> getOnlinePlayers(@NotNull PlayerCollection filter) {
        Collection<Player> filtered = new ArrayList<>();
        for (UUID uuid : uuids) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && !filter.contains(player)) {
                filtered.add(player);
            }
        }
        return filtered;
    }

    /**
     * @return a collection of OfflinePlayers
     */
    public @NotNull Collection<OfflinePlayer> getOfflinePlayers() {
        Collection<OfflinePlayer> filtered = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids) {
            filtered.add(Bukkit.getOfflinePlayer(uuid));
        }
        return filtered;
    }

    /**
     * @param filter players to exclude
     * @return a collection of OfflinePlayers
     */
    public @NotNull Collection<OfflinePlayer> getOfflinePlayers(@NotNull PlayerCollection filter) {
        Collection<OfflinePlayer> filtered = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (!filter.contains(player)) {
                filtered.add(player);
            }
        }
        return filtered;
    }

    /**
     * @param player the player
     * @return if the collection contains the player
     */
    public boolean contains(@NotNull Object player) {
        if (player instanceof Collection<?> collection) {
            for (Object object : collection) {
                if (!contains(object)) {
                    return false;
                }
            }
            return true;
        }
        return uuids.contains(getUUID(player));
    }

    public boolean add(@NotNull Object player) {
        if (player instanceof Collection<?> collection) {
            addAll(collection);
            return true;
        }
        return uuids.add(getUUID(player));
    }

    public void addAll(@NotNull Collection<?> players) {
        for (Object player : players) {
            add(player);
        }
    }

    public void addAll(@NotNull PlayerCollection players) {
        uuids.addAll(players.uuids);
    }

    public void addAll(@NotNull Object[] players) {
        for (Object player : players) {
            add(player);
        }
    }

    public boolean remove(@NotNull Object player) {
        return uuids.remove(getUUID(player));
    }

    public void removeAll(@NotNull Collection<?> players) {
        for (Object player : players) {
            remove(player);
        }
    }

    public void removeAll(@NotNull PlayerCollection players) {
        uuids.removeAll(players.uuids);
    }

    public void removeAll(@NotNull Object[] players) {
        for (Object player : players) {
            remove(player);
        }
    }

    public void clear() {
        uuids.clear();
    }

    public int size() {
        return uuids.size();
    }

    private UUID getUUID(Object object) {
        return (switch (object) {
            case UUID uuid -> uuid;
            case OfflinePlayer offline -> offline.getUniqueId();
            case PlayerWrapper fPlayer -> fPlayer.getUniqueId();
            case String string -> PlayerUtil.isValidUUID(string) ? UUID.fromString(string) : PlayerUtil.getUniqueIdFromName(string);
            default -> throw new IllegalArgumentException("Unsupported class type used");
        });
    }

    /* Iterable */

    @Override
    public Iterator<UUID> iterator() {
        return uuids.iterator();
    }

    @Override
    public Spliterator<UUID> spliterator() {
        return uuids.spliterator();
    }

    @Override
    public void forEach(Consumer<? super UUID> action) {
        uuids.forEach(action);
    }

    /* Serialization */

    /**
     * @return a List of Strings that can easily be used in a config
     */
    public @NotNull List<String> serialize() {
        List<String> filtered = new ArrayList<>(uuids.size());
        for (UUID uuid : uuids) {
            filtered.add(uuid.toString());
        }
        return filtered;
    }

}
