package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamSequencer;
import com.telefonica.topicmodel.streamprocess.StreamSequencerBackup;
import com.telefonica.topicmodel.views.VocabularyView;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class SequencerLauncherError {
    static final String inputTopic = "TOKENS.CALLS";
    static final String outputTopic = "SEQUENCES.CALLS";
    static final String vocabularyTopic = "TBL.VOCABULARY.CALLS";
    static final String stateDir = "/tmp/kafka-streams/topic.model.sequencer";
    static final String applicationId = "topic.model.sequencer";

    static final Logger logger = Logger.getLogger(MGM.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId, stateDir);
        final StreamsBuilder builder = new StreamsBuilder();

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());
        StreamSequencerBackup.create_stream(builder, streams,inputTopic, outputTopic, vocabularyTopic);

        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
