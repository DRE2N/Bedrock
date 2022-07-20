package de.erethon.bedrock.config.storage;

/**
 * @since 1.1.0
 * @author Fyreum
 */
public record StorageDataTranslator<T>(Class<T> type,
                                       StorageDataSerializer serializer,
                                       StorageDataDeserializer<T> deserializer) implements StorageDataSerializer, StorageDataDeserializer<T> {

    public StorageDataTranslator(Class<T> type, CompactSerializer serializer, CompactDeserializer<T> deserializer) {
        this(type, (StorageDataSerializer) serializer, (StorageDataDeserializer<T>) deserializer);
    }

    public StorageDataTranslator(Class<T> type, StorageDataSerializer serializer, CompactDeserializer<T> deserializer) {
        this(type, serializer, (StorageDataDeserializer<T>) deserializer);
    }

    public StorageDataTranslator(Class<T> type, CompactSerializer serializer, StorageDataDeserializer<T> deserializer) {
        this(type, (StorageDataSerializer) serializer, deserializer);
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public T deserialize(Object serialized, Class<?> type) {
        return deserializer.deserialize(serialized, type);
    }

    @Override
    public Object serialize(Object data, Class<?> exactType) {
        return serializer.serialize(data, exactType);
    }

}