package de.erethon.bedrock.config.storage;

import de.erethon.bedrock.config.ConfigUtil;
import de.erethon.bedrock.misc.ClassUtil;
import de.erethon.bedrock.misc.StringIgnoreCase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class manages the registrations for {@link StorageDataTranslator}s
 *
 * @since 1.1.0
 * @author Fyreum
 */
public class StorageDataTranslators {

    private static final Map<Class<?>, StorageDataTranslator<?>> dataTranslators = new HashMap<>();

    static {
        registerDataTranslator(new StorageDataTranslator<>(Boolean.class, o -> o, o -> (boolean) o));
        registerDataTranslator(new StorageDataTranslator<>(Double.class, o -> o, NumberConversions::toDouble));
        registerDataTranslator(new StorageDataTranslator<>(Float.class, o -> o, NumberConversions::toFloat));
        registerDataTranslator(new StorageDataTranslator<>(Integer.class, o -> o, NumberConversions::toInt));
        registerDataTranslator(new StorageDataTranslator<>(Long.class, o -> o, NumberConversions::toLong));
        registerDataTranslator(new StorageDataTranslator<>(Object.class, o -> o, o -> o));
        registerDataTranslator(new StorageDataTranslator<>(Short.class, o -> o, NumberConversions::toShort));
        registerDataTranslator(new StorageDataTranslator<>(String.class, o -> o, Object::toString));
        registerDataTranslator(new StorageDataTranslator<>(StringIgnoreCase.class, Object::toString, o -> new StringIgnoreCase(o.toString())));
        registerDataTranslator(new StorageDataTranslator<>(Location.class, o -> {
            Location loc = (Location) o;
            Map<String, Object> serialized = new HashMap<>(6);

            if (loc.isWorldLoaded()) {
                serialized.put("world", loc.getWorld().getName());
            }
            serialized.put("x", loc.getX());
            serialized.put("y", loc.getY());
            serialized.put("z", loc.getZ());
            serialized.put("yaw", loc.getYaw());
            serialized.put("pitch", loc.getPitch());
            return serialized;
        }, o -> {
            Map<String, Object> map = ConfigUtil.getMap(o);
            if (map.containsKey("world") && Bukkit.getWorld((String) map.get("world")) == null) {
                map.remove("world");
            }
            return Location.deserialize(map);
        }));
        registerDataTranslator(new StorageDataTranslator<>(UUID.class, Object::toString, (o) -> UUID.fromString((String) o)));
        registerDataTranslator(new StorageDataTranslator<>(Collection.class, o -> {
            Collection<?> list = (Collection<?>) o;
            if (list.isEmpty()) {
                return List.of();
            }
            Collection<Object> serializedList = new ArrayList<>(list.size());
            for (Object deserialized : list) {
                if (deserialized == null) {
                    continue;
                }
                Object serialized = get(deserialized.getClass()).serialize(deserialized, deserialized.getClass());
                serializedList.add(serialized);
            }
            return serializedList;
        }, o -> o instanceof Collection c ? c : List.of()));
        registerDataTranslator(new StorageDataTranslator<>(Map.class, o -> {
            Map<?, ?> map = (Map<?, ?>) o;
            if (map.isEmpty()) {
                return Map.of();
            }
            Map<Object, Object> serializedMap = new HashMap<>();
            map.forEach((k, v) -> {
                if (v == null) {
                    return;
                }
                Object key = get(k.getClass()).serialize(k, k.getClass());
                Object value = get(v.getClass()).serialize(v, v.getClass());
                serializedMap.put(key, value);
            });
            return serializedMap;
        }, ConfigUtil::getMap));
        registerDataTranslator(new StorageDataTranslator<>(Enum.class, (Object o) -> ((Enum<?>) o).name(), (o, t) -> {
            if (t.isEnum()) {
                for (Object enumConstant : t.getEnumConstants()) {
                    if (enumConstant.toString().equalsIgnoreCase((String) o)) {
                        return (Enum<?>) enumConstant;
                    }
                }
            }
            return null;
        }));
        registerDataTranslator(new StorageDataTranslator<>(Serializable.class, o -> {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(o);
                objectOutputStream.flush();
                return byteArrayOutputStream.toByteArray();
            } catch (IOException i) {
                i.printStackTrace();
                return null;
            }
        }, o -> {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[]) o);
                ObjectInput objectInputStream = new ObjectInputStream(byteArrayInputStream);
                return (Serializable) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException i) {
                i.printStackTrace();
                return null;
            }
        }));
    }

    /**
     * Register a StorageDataTranslator.
     *
     * @param translator the translator
     */
    public static void registerDataTranslator(@NotNull StorageDataTranslator<?> translator) {
        dataTranslators.put(ClassUtil.getClass(translator.getType()), translator);
    }

    /**
     * Returns the matching StorageDataTranslator for the provided type.
     * If no matching translator exists, this will return the default translator for Objects
     *
     * @param type the type
     * @return the matching StorageDataTranslator for the provided type
     */
    public static @NotNull StorageDataTranslator<?> get(@NotNull Class<?> type) {
        StorageDataTranslator<?> translator = dataTranslators.get(ClassUtil.getClass(type));
        if (translator == null) {
            if (type.isEnum()) {
                return dataTranslators.get(Enum.class);
            }
            if (ClassUtil.isImplementing(type, Collection.class)) {
                return dataTranslators.get(Collection.class);
            }
            if (ClassUtil.isImplementing(type, Map.class)) {
                return dataTranslators.get(Map.class);
            }
            return dataTranslators.get(Object.class);
        }
        return translator;
    }

}
