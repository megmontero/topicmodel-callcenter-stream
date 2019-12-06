package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamSequencer;
import com.telefonica.topicmodel.view.VocabularyView;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class SequencerLauncher {
    static final String inputTopic = "TOKENS.CALLS";
    static final String outputTopic = "SEQUENCES.CALLS";
    static final String vocabularyTopic = "TBL.VOCABULARY.CALLS";
    static final String stateDir = "/tmp/kafka-streams/topic.model.sequencer";
    static final String applicationId = "topic.model.sequencer";

    static final Logger logger = Logger.getLogger(SequencerLauncher.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId, stateDir);
        final StreamsBuilder builder = new StreamsBuilder();
        VocabularyView vocabularyView = new VocabularyView(vocabularyTopic);
        StreamSequencer.create_stream(builder,vocabularyView, inputTopic, outputTopic, vocabularyTopic);


        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());
        //streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
