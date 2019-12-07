package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import com.telefonica.topicmodel.streamprocess.StreamTokenizer;

public class TokenizerLauncher {
    static Config config = ConfigFactory.load();
    static final String inputTopic = config.getString("tokenizer.inputTopic");
    static final String outputTopic = config.getString("tokenizer.outputTopic");
    static final Logger logger = Logger.getLogger(TokenizerLauncher.class);
    static final String applicationId = config.getString("tokenizer.applicationId");
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
