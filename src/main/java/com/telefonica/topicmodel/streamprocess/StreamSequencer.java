package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.Sequence;
import com.telefonica.topicmodel.serdes.POJOClasses.Token;
import com.telefonica.topicmodel.view.VocabularyView;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamSequencer {
    private static Serde<POJOClasses.Token> tokenSerde;
    private static Serde<POJOClasses.Sequence> sequenceSerde;
    private final static int seguenceLenght = 866;
    static public void create_stream(final StreamsBuilder builder, final VocabularyView vocabularyView, final String inputTopic,
                                     final String outputTopic)
    {
        tokenSerde= JsonPOJOSerdes.getObjectSerde(Token.class);
        sequenceSerde = JsonPOJOSerdes.getObjectSerde(Sequence.class);
        KStream<String, Token> tokens = builder.stream(inputTopic, Consumed.with(Serdes.String(), tokenSerde));

        final KStream<String, Sequence> sequences = tokens.mapValues(
                token -> {

                    Sequence sequence = new Sequence();
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
                    POJOClasses.copy_commons(sequence,token);

                    return sequence;
                });

        sequences.to(outputTopic, Produced.with(Serdes.String(), sequenceSerde));
    }


}
