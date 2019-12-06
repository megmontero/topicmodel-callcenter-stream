package com.telefonica.topicmodel.streamprocess;


import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.pojos.PojosClasses.*;
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

import java.util.HashMap;
import java.util.Map;

public class StreamPredicter {

    private static Serde<Sequence> sequenceSerde;
    private static Serde<Topic> topicSerde;
    private final static int seguenceLenght = 866;

    static final Logger logger = Logger.getLogger(StreamPredicter.class);

    static public void create_stream(final StreamsBuilder builder, final String inputTopic,
                                     final String outputTopic, final String modelUrl)
    {
        initialize_serdes();
        KStream<String, Sequence> sequences = builder.stream(inputTopic, Consumed.with(Serdes.String(), sequenceSerde));


        final KStream<String, Topic> topics = sequences.mapValues(
                sequence -> {
                    Topic topic = new Topic();
                    TfModelInput input =new TfModelInput();
                    input.instances = new Integer[][] {sequence.sequence};
                    TfModelOutput output = CallSeqPredictModel.call(modelUrl, input);
                    if (output.error== null)
                        if (output.predictions!= null)
                            topic.predictions = output.predictions[0];
                        else
                            logger.error("HTTP errror");
                    else
                        topic.error = output.error;
                    PojosClasses.copy_commons(topic, sequence);

                    return topic;
                }
        );

        topics.to(outputTopic, Produced.with(Serdes.String(), topicSerde));
    }


    static private void initialize_serdes()
    {
        Map<String, Object> serdeProps = new HashMap<>();


        /*Topic Serdes*/
        final Serializer<Topic> topicSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Topic.class);
        topicSerializer.configure(serdeProps, false);

        final Deserializer<Topic> topicDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Topic.class);
        topicDeserializer.configure(serdeProps, false);
        topicSerde = Serdes.serdeFrom(topicSerializer, topicDeserializer);

        /*Sequence Serdes*/
        final Serializer<Sequence> sequenceSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Sequence.class);
        sequenceSerializer.configure(serdeProps, false);

        final Deserializer<Sequence> sequenceDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Sequence.class);
        sequenceDeserializer.configure(serdeProps, false);
        sequenceSerde = Serdes.serdeFrom(sequenceSerializer, sequenceDeserializer);
    }
}
