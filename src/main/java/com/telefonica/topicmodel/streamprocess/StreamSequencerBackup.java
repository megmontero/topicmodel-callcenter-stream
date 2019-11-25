package com.telefonica.topicmodel.streamprocess;


import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.serdes.JsonPOJODeserializer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamSequencerBackup {
    private static Serde<PojosClasses.Token> tokenSerde;
    private static Serde<PojosClasses.Sequence> sequenceSerde;
    private final static String VOCABULARY_STORE= "vocabulary-store";
    static public void create_stream(final StreamsBuilder builder, final KafkaStreams streams, final String inputTopic,
                                     final String outputTopic, final String vocabularyTopic)
    {
        initialize_serdes();
        KStream<String, PojosClasses.Token> tokens = builder.stream(inputTopic, Consumed.with(Serdes.String(), tokenSerde));
        final GlobalKTable<String, Integer> vocabulary =  builder.globalTable(vocabularyTopic,
                Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as(VOCABULARY_STORE)
                        .withKeySerde(Serdes.String()).withValueSerde(Serdes.Integer()));

        final ReadOnlyKeyValueStore<String, Integer> vocabularyView = streams.store(VOCABULARY_STORE, QueryableStoreTypes.keyValueStore());

        final KStream<String, PojosClasses.Sequence> sequences = tokens.mapValues(
                token -> {

                    PojosClasses.Sequence sequence = new PojosClasses.Sequence();
                    List<Integer> sequenceList = Arrays.asList(token.tokens).stream().map(tkn -> {
                        return vocabularyView.get(tkn);
                    }).collect(Collectors.toList());
                    sequence.sequence = sequenceList.toArray(sequence.sequence);
                    return sequence;
                });

        sequences.to(outputTopic, Produced.with(Serdes.String(), sequenceSerde));
    }


    static private void initialize_serdes()
    {
        Map<String, Object> serdeProps = new HashMap<>();


        /*Tokens Serdes*/
        final Serializer<PojosClasses.Token> tokenSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Token.class);
        tokenSerializer.configure(serdeProps, false);

        final Deserializer<PojosClasses.Token> tokenDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Token.class);
        tokenDeserializer.configure(serdeProps, false);
        tokenSerde = Serdes.serdeFrom(tokenSerializer, tokenDeserializer);

        /*Sequence Serdes*/
        final Serializer<PojosClasses.Sequence> sequenceSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Sequence.class);
        sequenceSerializer.configure(serdeProps, false);

        final Deserializer<PojosClasses.Sequence> sequenceDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Sequence.class);
        sequenceDeserializer.configure(serdeProps, false);
        sequenceSerde = Serdes.serdeFrom(sequenceSerializer, sequenceDeserializer);
    }
}
