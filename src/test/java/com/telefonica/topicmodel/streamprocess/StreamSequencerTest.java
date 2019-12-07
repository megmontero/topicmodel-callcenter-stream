package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
import com.telefonica.topicmodel.view.VocabularyView;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.log4j.Logger;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class StreamSequencerTest {
    static Config configApp = ConfigFactory.load();
    static final String inputTopic = "TOKENS.CALLS";
    static final String outputTopic = "SEQUENCES.CALLS";
    static final String applicationId = "StreamSequencerTest";
    private static Serde<Token> tokenSerde;
    private static  Serde<Sequence> sequenceSerde;
    private final static int seguenceLenght = configApp.getInt("sequencer.sequenceLenght");
    static final Logger logger = Logger.getLogger(StreamSequencerTest.class);
    private Properties config;

    @BeforeTest
    public void configure()
    {
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        sequenceSerde =  JsonPOJOSerdes.getObjectSerde(Sequence.class);
        tokenSerde = JsonPOJOSerdes.getObjectSerde(Token.class);

    }
    private Object[] testdata1()
    {
        Object []data = new Object[] {new String[] {"prueba", "llamada"}};
        return data;
    }

    private Object[] testdata2()
    {
        String []tokens = new String[seguenceLenght];
        java.util.Arrays.fill(tokens, 0, tokens.length, "prueba");
        Object []data = new Object[] {tokens};
        return data;
    }
    @DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod (){

        return new Object[][]{testdata1(), testdata2()};

    }

    @Test(dataProvider = "data-provider")
    public void streamSequencerTest1(String [] tokens) {

        final StreamsBuilder builder = new StreamsBuilder();
        VocabularyView vocabularyView;


        vocabularyView = Mockito.mock(VocabularyView.class);

        when(vocabularyView.get(any(String.class))).thenReturn(1);

        StreamSequencer.create_stream(builder,vocabularyView, inputTopic, outputTopic, seguenceLenght);

        final TopologyTestDriver streams = new TopologyTestDriver(builder.build(), config);

        ConsumerRecordFactory<String, Token> factory = new ConsumerRecordFactory<String, Token>(
                new StringSerializer(),
                tokenSerde.serializer()
        );
        /*Test Token*/
        Token token = new Token();
        token.call_text = "Buenos dias prueba de llamada mil cuatro";
        token.co_verint = "9867384343";
        token.co_province = "ES-CO";
        token.call_timestamp = new Long(1562893200);
        token.duration = 564;
        token.province = "Sur-Cordoba";
        token.tokens =  tokens;
        /*Expected sequences*/
        Integer[] expectedSequence =  new Integer[seguenceLenght];
        java.util.Arrays.fill(expectedSequence, 0, expectedSequence.length, 0);
        for(int i =1; i<=token.tokens.length;i++)
        {
            expectedSequence[expectedSequence.length -i] = 1;
        }




        streams.pipeInput(factory.create(inputTopic, token.co_verint, token));

        ProducerRecord<String, Sequence> outputRecord = streams.readOutput(
                outputTopic,
                new StringDeserializer(),
                sequenceSerde.deserializer()
        );

        Sequence s = outputRecord.value();
        logger.info("Sequences: " + Arrays.toString(s.sequence));
        streams.close();
        //OutputVerifier.compareKeyValue(outputRecord, expected.co_verint, expected);
        Assert.assertEquals(outputRecord.key(), token.co_verint);
        Assert.assertEquals(s.co_verint, token.co_verint);
        Assert.assertEquals(s.co_province, token.co_province);
        Assert.assertEquals(s.province, token.province);
        Assert.assertEquals(s.call_timestamp, token.call_timestamp);
        Assert.assertEquals(s.duration, token.duration);
        Assert.assertEquals(s.call_text, token.call_text);
        Assert.assertEquals(s.sequence, expectedSequence);

    }

}