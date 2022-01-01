package de.erethon.bedrock.config;

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
     * Returns a Map<String, Object> at the provided path or an empty one
     *
     * @param config the ConfigurationSection
     * @param path   the path
     * @return a Map<String, Object> at the provided path or an empty one
     */
    public static Map<String, Object> getMap(ConfigurationSection config, String path) {
        return getMap(config, path, false);
    }

    /**
     * Returns a Map<String, Object> at the provided path or an empty one
     *
     * @param config the ConfigurationSection
     * @param path   the path
     * @param deep   deep values
     * @return a Map<String, Object> at the provided path or an empty one
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
        for (Object o : list) {
            if (type.isInstance(o)) {
                finalList.add((T) o);
            }
        }
        return finalList;
    }

}
