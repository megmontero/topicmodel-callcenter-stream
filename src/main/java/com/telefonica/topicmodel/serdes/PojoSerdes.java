package com.telefonica.topicmodel.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class PojoSerdes {

    public void masa() {


    java.lang.Class serdeClass;

    }

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
