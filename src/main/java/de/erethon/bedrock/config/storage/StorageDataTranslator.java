package de.erethon.bedrock.config.storage;

/**
 * @author Fyreum
 */
public record StorageDataTranslator<T>(Class<T> type,
                                       StorageDataSerializer serializer,
                                       StorageDataDeserializer<T> deserializer) implements StorageDataSerializer, StorageDataDeserializer<T> {

    public Class<T> getType() {
        return type;
    }

    @Override
    public T deserialize(Object serialized) {
        return deserializer.deserialize(serialized);
    }

    @Override
    public Object serialize(Object data) {
        return serializer.serialize(data);
    }

}