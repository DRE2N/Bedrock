package de.erethon.bedrock.config.storage;

import de.erethon.bedrock.config.ConfigUtil;
import de.erethon.bedrock.misc.ClassUtil;
import de.erethon.bedrock.misc.StringIgnoreCase;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class manages the registrations for {@link StorageDataTranslator}s
 *
 * @author Fyreum
 */
public class StorageDataTranslators {

    private static final Map<Class<?>, StorageDataTranslator<?>> dataTranslators = new HashMap<>();

    static {
        registerDataTranslator(new StorageDataTranslator<>(Boolean.class, (o) -> o, (o) -> (boolean) o));
        registerDataTranslator(new StorageDataTranslator<>(Double.class, (o) -> o, (o) -> (double) o));
        registerDataTranslator(new StorageDataTranslator<>(Integer.class, (o) -> o, (o) -> (int) o));
        registerDataTranslator(new StorageDataTranslator<>(Long.class, (o) -> o, (o) -> (long) o));
        registerDataTranslator(new StorageDataTranslator<>(Object.class, (o) -> o, (o) -> o));
        registerDataTranslator(new StorageDataTranslator<>(Short.class, (o) -> o, (o) -> (short) o));
        registerDataTranslator(new StorageDataTranslator<>(String.class, (o) -> o, (o) -> (String) o));
        registerDataTranslator(new StorageDataTranslator<>(StringIgnoreCase.class, Object::toString, (o) -> new StringIgnoreCase((String) o)));
        registerDataTranslator(new StorageDataTranslator<>(Location.class, (o) -> ((Location) o).serialize(), (o) -> Location.deserialize(ConfigUtil.getMap(o))));
        registerDataTranslator(new StorageDataTranslator<>(UUID.class, Object::toString, (o) -> UUID.fromString((String) o)));
        registerDataTranslator(new StorageDataTranslator<>(Collection.class, (o) -> {
            Collection<?> list = (Collection<?>) o;
            if (list.isEmpty()) {
                return Collections.emptyList();
            }
            Collection<Object> serializedList = new ArrayList<>(list.size());
            for (Object deserialized : list) {
                if (deserialized == null) {
                    continue;
                }
                StorageDataTranslator<?> translator = get(deserialized.getClass());
                Object serialized = translator.serialize(deserialized);
                serializedList.add(serialized);
            }
            return serializedList;
        }, (o) -> o instanceof Collection c ? c : new ArrayList<>()));
        registerDataTranslator(new StorageDataTranslator<>(Map.class, (o) -> {
            Map<?, ?> map = (Map<?, ?>) o;
            if (map.isEmpty()) {
                return Map.of();
            }
            Map<Object, Object> serializedMap = new HashMap<>();
            map.forEach((k, v) -> {
                if (v == null) {
                    return;
                }
                Object key = get(k.getClass()).serialize(k);
                Object value = get(v.getClass()).serialize(v);
                serializedMap.put(key, value);
            });
            return serializedMap;
        }, ConfigUtil::getMap));
    }

    /**
     * Register a StorageDataTranslator.
     *
     * @param translator the translator
     */
    public static void registerDataTranslator(StorageDataTranslator<?> translator) {
        dataTranslators.put(ClassUtil.getClass(translator.getType()), translator);
    }

    /**
     * Returns the matching StorageDataTranslator for the provided type.
     * If no matching translator exists, this will return the default translator for Objects
     *
     * @param type the type
     * @return the matching StorageDataTranslator for the provided type
     */
    public static StorageDataTranslator<?> get(Class<?> type) {
        if (ClassUtil.isImplementing(type, Collection.class)) {
            return dataTranslators.get(Collection.class);
        }
        if (ClassUtil.isImplementing(type, Map.class)) {
            return dataTranslators.get(Map.class);
        }
        StorageDataTranslator<?> translator = dataTranslators.get(ClassUtil.getClass(type));
        if (translator == null) {
            return dataTranslators.get(Object.class);
        }
        return translator;
    }

}
