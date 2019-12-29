package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamSequencer;
import com.telefonica.topicmodel.streamprocess.StreamSequencerNew;
import com.telefonica.topicmodel.view.VocabularyView;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Laucher of microservice Sequencer. (without two KStreams)
 */
public class SequencerLauncherNew {

    static Config config = ConfigFactory.load();
    static final String inputTopic = config.getString("sequencer.inputTopic");
    static final String outputTopic = config.getString("sequencer.outputTopic");
    static final String vocabularyTopic = config.getString("sequencer.vocabularyTopic");
    static final String applicationId = "topic.model.sequencer";
    private final static int seguenceLenght = config.getInt("sequencer.sequenceLenght");

    static final Logger logger = Logger.getLogger(SequencerLauncherNew.class);

    /**
     * main class to start microservice Sequencer
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId);
        final StreamsBuilder builder = new StreamsBuilder();
        StreamSequencerNew.create_stream(builder,vocabularyTopic, inputTopic, outputTopic, seguenceLenght);


        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
