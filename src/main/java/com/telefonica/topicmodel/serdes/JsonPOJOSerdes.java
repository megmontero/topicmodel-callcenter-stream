package com.telefonica.topicmodel.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Static Class to get Serde of T class.
 */
public class JsonPOJOSerdes {

    /**
     * Get a Serde of a specific POJO Class.
     * @param serdeClass POJO Class
     * @param <T> POJO Class
     * @return Serde of POJO Class
     */
    public static <T> Serde<T>  getObjectSerde(Class serdeClass)
    {
        Map<String, Object> serdeProps = new HashMap<>();
        final Serializer<T> objectSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", serdeClass);
        objectSerializer.configure(serdeProps, false);

        final Deserializer<T> objectDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", serdeClass);
        objectDeserializer.configure(serdeProps, false);
        return  Serdes.serdeFrom(objectSerializer, objectDeserializer);

    };


}
