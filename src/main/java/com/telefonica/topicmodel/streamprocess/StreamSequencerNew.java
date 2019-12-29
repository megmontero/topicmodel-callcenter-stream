package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.Sequence;
import com.telefonica.topicmodel.serdes.POJOClasses.Token;
import com.telefonica.topicmodel.view.VocabularyView;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Stream to get Sequence from list of tokens and with vocabulary.
 *
 * Do not use a different view, using processor API instead.
 */
public class StreamSequencerNew {
    private static Serde<Token> tokenSerde;
    private static Serde<Sequence> sequenceSerde;
    final private static Logger logger = Logger.getLogger(StreamSequencer.class);
    private final static String VOCABULARY_STORE= "vocabulary-store";

    /**
     * Create stream for get sequence from Tokens topics.
     * @param builder Stream builder
     * @param vocabularyTopic Vocabulary topic to get id of words.
     * @param inputTopic Tokens topic.
     * @param outputTopic Sequence topic.
     * @param seguenceLenght Sequence Lenght.
     */
    static public void create_stream(final StreamsBuilder builder, final String vocabularyTopic, final String inputTopic,
                                     final String outputTopic, int seguenceLenght)
    {
        tokenSerde= JsonPOJOSerdes.getObjectSerde(Token.class);
        sequenceSerde = JsonPOJOSerdes.getObjectSerde(Sequence.class);
        KStream<String, Token> tokens = builder.stream(inputTopic, Consumed.with(Serdes.String(), tokenSerde));

        final GlobalKTable<String, String> vocabulary = builder.globalTable (vocabularyTopic,
                Materialized.  <String, String, KeyValueStore<Bytes, byte [] >> as
                        (VOCABULARY_STORE) .withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));



        final ValueTransformerSupplier<Token, Sequence> valueTransformerSupplier =
                () -> new ValueTransformer<Token, Sequence>() {
                    private   ReadOnlyKeyValueStore<String, String> vocabularyView;
                    @Override
                    public void init(final ProcessorContext context) {
                        vocabularyView = (ReadOnlyKeyValueStore) context.getStateStore(VOCABULARY_STORE);

                    }

                    @Override
                    public Sequence transform(final Token token) {
                        Sequence sequence = new Sequence();
                        if (token.tokens != null) {
                            List<Integer> sequenceList = Arrays.asList(token.tokens).stream().map(tkn -> {
                                Integer val =0;
                                try {
                                    val= Integer.parseInt(vocabularyView.get(tkn));
                                } catch (NumberFormatException e) {
                                    logger.error("Token not found in vocabulary: " + token);
                                }
                                return val;
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
                    }

                    @Override
                    public void close() { }
                };




        final KStream <String, Sequence> sequences = tokens.transformValues(valueTransformerSupplier);




        sequences.to(outputTopic, Produced.with(Serdes.String(), sequenceSerde));
    }


}
