package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Properties;
import org.apache.log4j.Logger;


public class StreamTokenizerTest {
    final static String applicationId = "StreamTokenizerTest";
    static final String inputTopic = "CALLS";
    static final String outputTopic = "TOKENS.CALLS";
    private static Serde<Token> tokenSerde;
    private static  Serde<Call> callSerde;
    static final Logger logger = Logger.getLogger(StreamTokenizerTest.class);
    private Properties config;

    @BeforeTest
    public void configure()
    {
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        callSerde =  JsonPOJOSerdes.getObjectSerde(Call.class);
        tokenSerde = JsonPOJOSerdes.getObjectSerde(Token.class);

    }


    private Object[] testdata1()
    {
        /*control_type, predictions, pred_trype, controlSuccess*/


        Object []data = new Object[] {"Buenos dias prueba de llamada mil cuatro",
                new String[] {"prueba", "llamada"}};


        return data;
    }

    private Object[] testdata2()
    {
        /*control_type, predictions, pred_trype, controlSuccess*/


        Object []data = new Object[] {"., MAYúSCULA EÑE",
                new String[] {"mayuscula", "ene"}};


        return data;
    }

    @DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod (){

        return new Object[][]{testdata1(), testdata2()};

    }

    @Test(dataProvider = "data-provider")
    public void streamTokenizerTest1(String call_text, String[] expectedTokens) {

        final StreamsBuilder builder = new StreamsBuilder();
        StreamTokenizer.create_stream(builder, inputTopic, outputTopic);

        final TopologyTestDriver streams = new TopologyTestDriver(builder.build(), config);

        ConsumerRecordFactory<String, Call> factory = new ConsumerRecordFactory<String, Call>(
                new StringSerializer(),
                callSerde.serializer()
        );
        /*Test call*/
        Call call = new Call();
        call.call_text = call_text;
        call.co_verint = "9867384343";
        call.co_province = "ES-CO";
        call.call_timestamp = new Long(1562893200);
        call.duration = 564;
        call.province = "Sur-Cordoba";



        streams.pipeInput(factory.create(inputTopic, call.co_verint, call));

        ProducerRecord<String, Token> outputRecord = streams.readOutput(
                outputTopic,
                new StringDeserializer(),
                tokenSerde.deserializer()
        );

        Token t = outputRecord.value();
        logger.info("Tokens: " + Arrays.toString(t.tokens));
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