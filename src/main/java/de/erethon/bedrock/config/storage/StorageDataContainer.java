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
    }

    /**
     * Run the default loading process
     */
    protected void defaultLoadProcess() {
        loadDataFields();
        loadInitialValues();
        initialize();
        load();
    }

    /**
     * Load the data fields from this container
     *
     * @since 1.2.5
     */
    protected void loadDataFields() {
        loadDataFields(this, "");
    }

    private void loadDataFields(Object valueContainer, String subPath) {
        Field[] fields = valueContainer.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(StorageData.class)) {
                try {
                    dataFields.add(new StorageDataField(valueContainer, field, subPath));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (field.isAnnotationPresent(AdditionalContainer.class)) {
                String sub = field.getAnnotation(AdditionalContainer.class).subPath();
                try {
                    field.setAccessible(true);
                    loadDataFields(field.get(valueContainer), subPath + sub);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Load the initial data from the config file
     */
    protected void loadInitialValues() {
        for (StorageDataField field : dataFields) {
            try {
                field.loadInitialValue();
            } catch (IllegalAccessException e) {
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
