package com.telefonica.topicmodel.streamprocess;


import com.telefonica.topicmodel.model.ModelBajaFactura;
import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
import com.telefonica.topicmodel.serdes.JsonPOJODeserializer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;
import java.util.List;

/**
 * Stream for predict call topic from Sequence.
 */
public class StreamPredicter {

    private static Serde<Sequence> sequenceSerde;
    private static Serde<Topic> topicSerde;

    static final Logger logger = Logger.getLogger(StreamPredicter.class);

    /**
     * Create stream for predict call topic from Sequence.
     * Read sequence from input topic and write it on output topic.
     * @param builder Stream Builder
     * @param inputTopic Kafka Input Topic.
     * @param outputTopic Kafka output Topic.
     * @param modelUrl URL of tensorflow serving model.
     * @param modelId ID of model.
     * @param labels List of model labels.
     */
    static public void create_stream(final StreamsBuilder builder, final String inputTopic,
                                     final String outputTopic, String  modelUrl, String modelId, List<String> labels)
    {
        sequenceSerde = JsonPOJOSerdes.getObjectSerde(Sequence.class);
        topicSerde = JsonPOJOSerdes.getObjectSerde(Topic.class);
        KStream<String, Sequence> sequences = builder.stream(inputTopic, Consumed.with(Serdes.String(), sequenceSerde));


        final KStream<String, Topic> topics = sequences.mapValues(
                sequence -> {
                    ModelBajaFactura model = new ModelBajaFactura(sequence, modelUrl, modelId, labels);
                    Topic topic = model.get_topic();
                    return topic;
                }
        );

        topics.to(outputTopic, Produced.with(Serdes.String(), topicSerde));
    }


}
