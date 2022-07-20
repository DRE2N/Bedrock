package de.erethon.bedrock.config.storage;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.misc.ClassUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since 1.1.0
 * @author Fyreum
 */
public final class StorageDataField {

    private final Field field;
    private final String path;
    private final Class<?> type;
    private final Class<?>[] keyTypes;
    private final Class<?>[] valueTypes;
    private final boolean initialize;
    private final boolean log;
    private final boolean debug;
    private final Nullability nullability;
    private final String forbiddenNullMessage;
    private final StorageDataSave saveSetting;
    private Object initialValue;
    private int loadedHashCode;

    protected StorageDataField(Field field, String subPath) {
        StorageData annotation = field.getAnnotation(StorageData.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Missing annotation " + StorageData.class.getName());
        }
        this.field = field;
        this.path = finalPath(subPath, annotation.path());
        Class<?> exactType = annotation.type();
        this.type = ClassUtil.getClass(exactType != StorageData.DefaultTypeClass.class ? exactType : field.getType());
        this.keyTypes = annotation.keyTypes();
        this.valueTypes = annotation.valueTypes();
        this.initialize = annotation.initialize();
        this.log = annotation.log();
        this.debug = annotation.debug();
        this.nullability = annotation.nullability();
        this.forbiddenNullMessage = annotation.forbiddenNullMessage().isEmpty() ? "Illegal null value at '" + path + "' was found" : annotation.forbiddenNullMessage();
        this.saveSetting = annotation.save();
    }

    private String finalPath(String subPath, String path) {
        if (path.contains("#")) {
            if (subPath.isEmpty()) {
                throw new IllegalArgumentException("Illegal character '#' found in path");
            }
            return subPath;
        }
        return subPath + (path.isEmpty() ? field.getName() : path);
    }

    protected void loadInitialValue(StorageDataContainer container) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        this.initialValue = getValue(container);
    }

    private Object serialize(Object value) throws NullPointerException {
        if (value == null) {
            return null;
        }
        StorageDataTranslator<?> translator = StorageDataTranslators.get(type);
        return translator.serialize(value, type);
    }

    private Object deserialize(Object value, Class<?> type, int keyIndex, int valueIndex) throws NullPointerException {
        StorageDataTranslator<?> translator = StorageDataTranslators.get(type);
        Object deserialized = translator.deserialize(value, type);

        if (deserialized instanceof Collection<?> list) {
            if (list.isEmpty()) {
                return list;
            }
            Collection<Object> deserializedList = new ArrayList<>(list.size());
            for (Object serialized : list) {
                Object element = deserialize(serialized, valueTypes.length <= valueIndex ? Object.class : valueTypes[valueIndex], keyIndex, valueIndex + 1);
                deserializedList.add(element);
            }
            return deserializedList;
        } else if (deserialized instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                return map;
            }
            Map<Object, Object> deserializedMap = new HashMap<>(map.size());
            for (Object serializedKey : map.keySet()) {
                Object key = deserialize(serializedKey, keyTypes.length <= keyIndex ? Object.class : keyTypes[keyIndex], keyIndex + 1, valueIndex);
                Object v = deserialize(map.get(serializedKey), valueTypes.length <= valueIndex ? Object.class : valueTypes[valueIndex], keyIndex, valueIndex + 1);
                deserializedMap.put(key, v);
            }
            return deserializedMap;
        }
        return deserialized;
    }

    protected void initialize(StorageDataContainer container) {
        if (!initialize) {
            return;
        }
        log("Initializing value '" + path + "'...");
        debug("Initializing value '" + initialValue + "' at '" + path + "'...");

        FileConfiguration config = container.getConfig();
        if (config.contains(path)) {
            debug("Won't initialize value '" + path + "': Already present");
            return;
        }
        config.set(path, serialize(initialValue));
    }

    protected void load(StorageDataContainer container) throws NullPointerException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Object value = container.getConfig().get(path);
        if (value instanceof ConfigurationSection section) { // convert ConfigurationSection to Map<String, Object>
            value = section.getValues(false);
        }
        if (value != null) {
            try {
                value = deserialize(value, type, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
                value = null;
            }
        }
        if (value == null) {
            switch (nullability) {
                case FORBID:
                    MessageUtil.log(forbiddenNullMessage);
                case IGNORE:
                    debug("Won't load value '" + path + "': Null value");
                    return;
                default:
                    value = ClassUtil.getNullValue(type);
                    break;
            }
        }
        loadedHashCode = Objects.hash(value);
        if (value instanceof Collection c) {
            if (initialValue instanceof Collection<?> list) { // check if the initial value is already a Collection
                debug("Loading value '" + value + "' from '" + path + "'...");
                list.addAll(c);
                return;
            } else if (!type.getName().equals(Object.class.getName()) && !type.getName().equals(ArrayList.class.getName())) {
                Constructor<? extends Collection> constructor = (Constructor<? extends Collection>) type.getDeclaredConstructor(Collection.class);
                constructor.setAccessible(true);
                value = constructor.newInstance(value);
            }
        } else if (value instanceof Map m) { // check if the initial value is already a Map
            if (initialValue instanceof Map<?, ?> map) {
                debug("Loading value '" + value + "' from '" + path + "'...");
                map.putAll(m);
                return;
            } else if (!type.getName().equals(Object.class.getName()) && !type.getName().equals(HashMap.class.getName())) {
                Constructor<? extends Map> constructor = (Constructor<? extends Map>) type.getDeclaredConstructor(Map.class);
                constructor.setAccessible(true);
                value = constructor.newInstance(value);
            }
        }
        log("Loading value '" + path + "'...");
        debug("Loading value '" + value + "' from '" + path + "'...");

        field.set(container, value);
    }

    protected void save(StorageDataContainer container) throws IllegalAccessException {
        if (saveSetting == StorageDataSave.NONE) {
            return;
        }
        Object value = getValue(container);
        if (saveSetting == StorageDataSave.CHANGES && loadedHashCode == Objects.hash(value)) {
            debug("Won't save value '" + path + "': No changes found");
            return;
        }
        if (value == null) {
            if (nullability == Nullability.IGNORE | nullability == Nullability.FORBID) {
                debug("Won't save value '" + path + "': Null value");
                return;
            }
        } else {
            value = serialize(value);
        }
        log("Saving value '" + path + "'...");
        debug("Saving value '" + value + "' at '" + path + "'...");

        container.getConfig().set(path, value);
    }

    private void log(String msg) {
        if (log) {
            MessageUtil.log(msg);
        }
    }

    private void debug(String msg) {
        if (debug) {
            MessageUtil.log(msg);
        }
    }

    /* getter */

    /**
     * @return the object field
     */
    public Field getField() {
        return field;
    }

    /**
     * @return the object type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * @return the map key types
     */
    public Class<?>[] getKeyTypes() {
        return keyTypes;
    }

    /**
     * @return the map/collection value types
     */
    public Class<?>[] getValueTypes() {
        return valueTypes;
    }

    /**
     * @return the path where the object is stored at
     */
    public String getPath() {
        return path;
    }

    /**
     * @return true if the config value will get initialized, false otherwise
     */
    public boolean isInitialize() {
        return initialize;
    }

    /**
     * @return true if the storing process will get logged, false otherwise
     */
    public boolean isLog() {
        return log;
    }

    /**
     * @return true if storing process will get debugged, false otherwise
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @return the nullability of the object value
     */
    public Nullability getNullability() {
        return nullability;
    }

    /**
     * @return the message for forbidden nullability
     */
    public String getForbiddenNullMessage() {
        return forbiddenNullMessage;
    }

    /**
     * @return the save setting
     */
    public StorageDataSave getSaveSetting() {
        return saveSetting;
    }

    /**
     * @return the initial object value
     */
    public Object getInitialValue() {
        return initialValue;
    }

    /**
     * @return the hash code of the loaded value
     */
    public int getLoadedHashCode() {
        return loadedHashCode;
    }

    protected Object getValue(StorageDataContainer container) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(container);
    }

}
