package com.telefonica.topicmodel.serdes;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Class for serialize POJO Class to Byte
 * @param <T>
 */
public class JsonPOJOSerializer<T> implements Serializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default constructor needed by Kafka
     */
    public JsonPOJOSerializer() {
    }

    /**
     * Serializer configure method.
     * @param props
     * @param isKey
     */
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
    }

    /**
     * Serializer from POJO Class to Json Bytes.
     * @param topic key
     * @param data POJO Object
     * @return Byte array
     */
    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null)
            return null;

        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }
    /**
     * Needed by serializer.
     */
    @Override
    public void close() {
    }

}