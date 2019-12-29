package com.telefonica.topicmodel.config;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import java.util.Properties;


/**
 * Class for config Kafka Streams
 */
public class StreamConfig {
    final private Config config = ConfigFactory.load();
    final private Properties streamsConfiguration;

    /**
     * Constructor of StreamConfig
     * @param applicationId Id of Stream application.
     */
    public StreamConfig(String applicationId){
        streamsConfiguration = new Properties();

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, applicationId);
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
                config.getString("kafkaClient.bootstrapServers"));
        // Specify default (de)serializers for record keys and for record values.
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        streamsConfiguration.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG,
                config.getString("kafkaClient.securityProtocol"));
        streamsConfiguration.put("sasl.mechanism",
                config.getString("kafkaClient.saslMechanism"));
        streamsConfiguration.put("sasl.jaas.config",
                config.getString("kafkaClient.saslJaas"));



    }

    /**
     * Constructor with stateDir.
     * @param applicationId Id of application.
     * @param stateDir Dir to store state.
     */
    public StreamConfig(String applicationId, String stateDir){
        this(applicationId);
        streamsConfiguration.put("state.dir", stateDir);

    }

    /**
     * Get stream properties
     * @return Properties of streams.
     */
    public Properties get_properties(){
        return streamsConfiguration;
    }


}
