package com.telefonica.topicmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.streamprocess.StreamPredicter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.http.impl.client.*;


public class PredicterLauncher {
    static final String inputTopic = "SEQUENCES.CALLS";
    static final String outputTopic = "TOPICS.CALLS";
    static final String applicationId = "topic.model.predicter";
    static final String modelUrl = "http://tf-baja-model/v1/models/baja:predict";

    static final Logger logger = Logger.getLogger(PredicterLauncher.class);
    public static void main(String[] args) {

        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig(applicationId);
        final StreamsBuilder builder = new StreamsBuilder();

        StreamPredicter.create_stream(builder, inputTopic, outputTopic, modelUrl);


        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());
        //streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }


}
