package de.erethon.bedrock.config;

import de.erethon.bedrock.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daniel Saukel, Fyreum
 */
public class ConfigUtil {

    /**
     * Returns a {@literal Map<String, Object>} at the provided path or an empty one
     *
     * @param config the ConfigurationSection
     * @param path   the path
     * @return a {@literal Map<String, Object>}  at the provided path or an empty one
     */
    public static Map<String, Object> getMap(ConfigurationSection config, String path) {
        return getMap(config, path, false);
    }

    /**
     * Returns a {@literal Map<String, Object>}  at the provided path or an empty one
     *
     * @param config the ConfigurationSection
     * @param path   the path
     * @param deep   deep values
     * @return a {@literal Map<String, Object>}  at the provided path or an empty one
     */
    public static Map<String, Object> getMap(ConfigurationSection config, String path, boolean deep) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            return section.getValues(deep);
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Returns a {@literal Map<String, Object>}  of the provided object or an empty one
     *
     * @param obj    the object
     * @return a {@literal Map<String, Object>}  of the provided object or an empty one
     */
    public static Map<String, Object> getMap(Object obj) {
        return getMap(obj, false);
    }

    /**
     * Returns a {@literal Map<String, Object>}  of the provided object or an empty one
     *
     * @param obj    the object
     * @param deep   deep values
     * @return a {@literal Map<String, Object>}  of the provided object or an empty one
     */
    public static Map<String, Object> getMap(Object obj, boolean deep) {
        if (obj == null) {
            return new HashMap<>();
        }
        try {
            if (obj instanceof Map) {
                return (Map<String, Object>) obj;
            } else {
                ConfigurationSection section = (ConfigurationSection) obj;
                return section.getValues(deep);
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * Returns a List with given element type at the provided path or an empty one
     *
     * @param config the ConfigurationSection
     * @param path   the path
     * @param type   the element type
     * @return a List with given element type at the provided path or an empty one
     */
    public static <T> List<T> getList(ConfigurationSection config, String path, Class<T> type) {
        List<?> list = config.getList(path);
        List<T> finalList = new ArrayList<>();
        if (list == null) {
            return finalList;
        }
        for (Object o : list) {
            if (type.isInstance(o)) {
                finalList.add(type.cast(o));
            }
        }
        return finalList;
    }

    public static Location getLocation(ConfigurationSection config, String path) {
        return getLocation(config, path, true);
    }

    public static Location getLocation(ConfigurationSection config, String path, boolean log) {
        String finalPath = path == null ? "" : path + ".";
        String worldName = config.getString(finalPath + "world");
        if (worldName == null || worldName.isEmpty()) {
            if (log) {
                MessageUtil.log("World name was not found");
            }
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null && log) {
            MessageUtil.log("Couldn't find world '" + worldName + "'");
        }
        double x = config.getDouble(finalPath + "x", 0);
        double y = config.getDouble(finalPath + "y", 0);
        double z = config.getDouble(finalPath + "z", 0);
        float yaw = (float) config.getDouble(finalPath + "yaw", 0);
        float pitch = (float) config.getDouble(finalPath + "pitch", 0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setLocation(ConfigurationSection config, String path, Location loc) {
        String finalPath = path == null ? "" : path + ".";
        config.set(finalPath + "world", loc.getWorld().getName());
        config.set(finalPath + "x", loc.getX());
        config.set(finalPath + "y", loc.getY());
        config.set(finalPath + "z", loc.getZ());
        config.set(finalPath + "yaw", loc.getYaw());
        config.set(finalPath + "pitch", loc.getPitch());
    }

}
