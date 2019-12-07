package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.serdes.PojoSerdes;
import com.telefonica.topicmodel.serdes.PojosClasses;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.Properties;


public class StreamTokenizerTest {
    final static String applicationId = "StreamTokenizerTest";
    static final String inputTopic = "CALLS";
    static final String outputTopic = "TOKENS.CALLS";
    private static Serde<PojosClasses.Token> tokenSerde;
    private static  Serde<PojosClasses.Call> callSerde;
    //static final Logger logger = Logger.getLogger(StreamTokenizerTest.class);
    private Properties config;

    /*@BeforeTest*/
    public void configure()
    {
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        callSerde =  PojoSerdes.getObjectSerde(PojosClasses.Call.class);
        tokenSerde = PojoSerdes.getObjectSerde(PojosClasses.Token.class);

    }
    @Test
    public void streamTokenizerTest1() {
        configure();

        final StreamsBuilder builder = new StreamsBuilder();
        StreamTokenizer.create_stream(builder, inputTopic, outputTopic);

        final TopologyTestDriver streams = new TopologyTestDriver(builder.build(), config);

        ConsumerRecordFactory<String, PojosClasses.Call> factory = new ConsumerRecordFactory<String, PojosClasses.Call>(
                new StringSerializer(),
                callSerde.serializer()
        );
        /*Test call*/
        PojosClasses.Call call = new PojosClasses.Call();
        call.call_text = "Buenos dias prueba de llamada mil cuatro";
        call.co_verint = "9867384343";
        call.co_province = "ES-CO";
        call.call_timestamp = new Long(555555555);
        call.duration = 564;
        call.province = "Sur-Cordoba";
        /*Expected tokens*/
        String[] expectedTokens =  new String[] {"prueba", "llamada"};


        streams.pipeInput(factory.create(inputTopic, call.co_verint, call));

        ProducerRecord<String, PojosClasses.Token> outputRecord = streams.readOutput(
                outputTopic,
                new StringDeserializer(),
                tokenSerde.deserializer()
        );

        PojosClasses.Token t = outputRecord.value();
        //logger.info("Tokens: " + Arrays.toString(t.tokens));
        streams.close();
        //OutputVerifier.compareKeyValue(outputRecord, expected.co_verint, expected);
        Assert.assertEquals(outputRecord.key(), call.co_verint);
        Assert.assertEquals(t.co_verint, call.co_verint);
        Assert.assertEquals(t.co_province, call.co_province);
        Assert.assertEquals(t.province, call.province);
        Assert.assertEquals(t.call_timestamp, call.call_timestamp);
        Assert.assertEquals(t.duration, call.duration);
        Assert.assertEquals(t.call_text, call.call_text);
        Assert.assertEquals(t.tokens, expectedTokens);

    }

}