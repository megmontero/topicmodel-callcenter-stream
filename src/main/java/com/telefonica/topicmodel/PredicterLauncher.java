package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamPredicter;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


public class PredicterLauncher {
    static final String inputTopic = "SEQUENCES.CALLS";
    static final String outputTopic = "TOPICS.CALLS";
    static final String applicationId = "topic.model.predicter";

    static final Logger logger = Logger.getLogger(PredicterLauncher.class);
    public static void main(String[] args) {

        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId);
        final StreamsBuilder builder = new StreamsBuilder();

        StreamPredicter.create_stream(builder, inputTopic, outputTopic);


        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
