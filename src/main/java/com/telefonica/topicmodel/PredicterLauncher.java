package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamPredicter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Laucher of microservice Predicter
 */
public class PredicterLauncher {

    static Config config = ConfigFactory.load();
    static final String inputTopic =  config.getString("predicter.inputTopic");
    static final String outputTopic = config.getString("predicter.outputTopic");
    static final String applicationId = config.getString("predicter.applicationId");
    static final String modelUrl = config.getString("modelBajaFactura.modelUrl");
    static final String modelId = config.getString("modelBajaFactura.modelId");
    static final List<String> labels = config.getStringList("modelBajaFactura.modelLabels");

    static final Logger logger = Logger.getLogger(PredicterLauncher.class);

    /**
     * main class to start microservice Predicter
     * @param args
     */
    public static void main(String[] args) {

        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId);
        final StreamsBuilder builder = new StreamsBuilder();

        StreamPredicter.create_stream(builder, inputTopic, outputTopic,
                modelUrl, modelId, labels);

        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());

        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
