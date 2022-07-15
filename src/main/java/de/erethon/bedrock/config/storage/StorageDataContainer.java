package de.erethon.bedrock.config.storage;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.config.EConfig;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.1.0
 * @author Fyreum
 */
public class StorageDataContainer extends EConfig {

    private final List<StorageDataField> dataFields = new ArrayList<>();

    public StorageDataContainer(File file, int configVersion) {
        super(file, configVersion);
        loadDataFields(getClass(), "");
    }

    private void loadDataFields(Class<?> clazz, String subPath) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(StorageData.class)) {
                try {
                    dataFields.add(new StorageDataField(field, subPath));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (field.isAnnotationPresent(AdditionalContainer.class)) {
                String sub = field.getAnnotation(AdditionalContainer.class).subPath();
                loadDataFields(field.getClass(), subPath + sub);
            }
        }
    }

    /**
     * Run the default loading process
     */
    protected void defaultLoadProcess() {
        loadInitialValues();
        initialize();
        load();
    }

    /**
     * Load the initial data from the config file
     */
    protected void loadInitialValues() {
        for (StorageDataField field : dataFields) {
            try {
                field.loadInitialValue(this);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initial setup
     */
    @Override
    public void initialize() {
        for (StorageDataField field : dataFields) {
            field.initialize(this);
        }
        super.save();
    }

    /**
     * Load the data from the config file
     */
    @Override
    public void load() {
        for (StorageDataField field : dataFields) {
            try {
                field.load(this);
            } catch (NullPointerException e) {
                MessageUtil.log("No data translator for " + field.getType().getName() + " found");
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the stored data into the config file
     */
    public void saveData() {
        for (StorageDataField field : dataFields) {
            try {
                field.save(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.save();
    }

}
