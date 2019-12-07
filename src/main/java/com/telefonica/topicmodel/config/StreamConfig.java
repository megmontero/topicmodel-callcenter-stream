package com.telefonica.topicmodel.config;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class StreamConfig {
    final private Config config = ConfigFactory.load();
    final private Properties streamsConfiguration;
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

    public StreamConfig(String applicationId, String stateDir){
        this(applicationId);
        streamsConfiguration.put("state.dir", stateDir);

    }

    public Properties get_properties(){
        return streamsConfiguration;
    }


}
