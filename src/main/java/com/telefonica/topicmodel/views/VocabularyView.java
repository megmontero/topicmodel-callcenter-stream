package com.telefonica.topicmodel.views;

import com.telefonica.topicmodel.PredicterLauncher;
import com.telefonica.topicmodel.config.StreamConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.log4j.Logger;

public class VocabularyView {
    private final static String VOCABULARY_STORE= "vocabulary-store";
    private final ReadOnlyKeyValueStore<String, String> vocabularyView;
    static final String stateDir = "/tmp/kafka-streams/topic.model.view";
    static final String applicationId = "topic.model.vocabulary";
    final Logger logger;
    public VocabularyView(String vocabularyTopic) {
        logger = Logger.getLogger(VocabularyView.class);

        final StreamConfig streamsConfiguration = new StreamConfig(applicationId,stateDir);
        final StreamsBuilder builder = new StreamsBuilder();
        final GlobalKTable<String, String> vocabulary =  builder.globalTable(vocabularyTopic,
                Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(VOCABULARY_STORE)
                        .withKeySerde(Serdes.String()).withValueSerde(Serdes.String()));

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());
        streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        vocabularyView = streams.store(VOCABULARY_STORE, QueryableStoreTypes.keyValueStore());
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        // Esperamos antes de lanzar ningun otro stream
        try {
            Thread.sleep(5000);
        }
        catch (Exception e)
        {
            System.out.print("Exception");
        }
    }

    public Integer get(String token)
    {

        try {
            return Integer.parseInt(vocabularyView.get(token));
        } catch (NumberFormatException e) {
            logger.error("Token not found in vocabulary: " + token);
            return 0;
        }

    }
}
