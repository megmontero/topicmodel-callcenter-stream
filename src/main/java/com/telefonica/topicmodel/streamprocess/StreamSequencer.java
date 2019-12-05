package com.telefonica.topicmodel.streamprocess;


import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.serdes.JsonPOJODeserializer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerializer;
import com.telefonica.topicmodel.views.VocabularyView;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamSequencer {
    private static Serde<PojosClasses.Token> tokenSerde;
    private static Serde<PojosClasses.Sequence> sequenceSerde;
    private final static int seguenceLenght = 866;
    static public void create_stream(final StreamsBuilder builder, final VocabularyView vocabularyView, final String inputTopic,
                                     final String outputTopic, final String vocabularyTopic)
    {
        initialize_serdes();
        KStream<String, PojosClasses.Token> tokens = builder.stream(inputTopic, Consumed.with(Serdes.String(), tokenSerde));

        final KStream<String, PojosClasses.Sequence> sequences = tokens.mapValues(
                token -> {

                    PojosClasses.Sequence sequence = new PojosClasses.Sequence();
                    if (token.tokens != null) {
                        List<Integer> sequenceList = Arrays.asList(token.tokens).stream().map(tkn -> {
                            return vocabularyView.get(tkn);
                        }).collect(Collectors.toList());
                        sequence.sequence = new Integer[seguenceLenght];
                        Arrays.fill(sequence.sequence , 0);
                        int start =  seguenceLenght - sequenceList.size();
                        if (start <0)
                            start=0;
                        for (int i= start; i<seguenceLenght; i++)
                        {
                            sequence.sequence[i] = sequenceList.get(i - start);
                        }
                    }
                    sequence.call_timestamp = token.call_timestamp;
                    sequence.call_text = token.call_text;
                    sequence.co_province = token.co_province;
                    sequence.province = token.province;
                    sequence.duration = token.duration;
                    sequence.start_time = token.start_time;

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
