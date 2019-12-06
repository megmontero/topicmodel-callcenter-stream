package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import com.telefonica.topicmodel.streamprocess.StreamTokenizer;

public class TokenizerLauncher {
    static final String inputTopic = "CALLS";
    static final String outputTopic = "TOKENS.CALLS";
    static final Logger logger = Logger.getLogger(TokenizerLauncher.class);
    static final String applicationId = "topic.model.tokenizer";
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId);
        final StreamsBuilder builder = new StreamsBuilder();
        StreamTokenizer.create_stream(builder, inputTopic, outputTopic);

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
