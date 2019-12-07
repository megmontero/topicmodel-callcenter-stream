package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses.Sequence;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelInput;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelOutput;
import com.telefonica.topicmodel.serdes.POJOClasses.Topic;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@PrepareForTest(CallSeqPredictModel.class)
@PowerMockIgnore({"org.apache.log4j.*"})
public class StreamPredicterTest  extends PowerMockTestCase {
    static final String inputTopic = "SEQUENCES.CALLS";
    static final String outputTopic = "TOPICS.CALLS";
    static final String applicationId = "StreamPredicterTest";
    private static Serde<Topic> topicSerde;
    private static  Serde<Sequence> sequenceSerde;
    static final Logger logger = Logger.getLogger(StreamPredicterTest.class);
    private Properties config;
    static Config configApp = ConfigFactory.load();
    static final String modelUrl =  "dummy";
    static final String modelId =  "dummy";
    static final List<String> labels = configApp.getStringList("modelBajaFactura.modelLabels");



    private Object[] testdata1()
    {
        /*control_type, predictions, pred_trype, controlSuccess*/


        Object []data = new Object[] {"Baja",
                new Float[]{ new Float(0.6), new Float(0.2), new Float(0.2),},
                        "Baja", true};


        return data;
    }

    private Object[] testdata2()
    {
        /*control_type, predictions, pred_trype, controlSuccess*/
        Object []data = new Object[] {"Baja",
                new Float[]{ new Float(0.3), new Float(0.2), new Float(0.5)},
                "Resto", false};
        return data;
    }

    private Object[] testdata3()
    {
        /*control_type, predictions, pred_type, controlSuccess*/
        Object []data = new Object[] {"Factura",
                new Float[]{ new Float(0.3), new Float(0.7), new Float(0)},
                "Factura", true};
        return data;
    }

    private Object[] testdata4()
    {
        /*control_type, predictions, pred_trype, controlSuccess*/
        Object []data = new Object[] {"Baja",
                new Float[]{ new Float(0.3), new Float(0.6), new Float(0.1)},
                "Factura", false};
        return data;
    }


    @DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod (){

        return new Object[][]{testdata1(), testdata2(), testdata3(), testdata4()};

    }


    @BeforeTest
    public void configure()
    {
        config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        sequenceSerde =  JsonPOJOSerdes.getObjectSerde(Sequence.class);
        topicSerde = JsonPOJOSerdes.getObjectSerde(Topic.class);
    }

    @Test(dataProvider = "data-provider")
    public void streamPredicterTest1(String control_type, Float[] expectedPredictions,
                                     String expectedPredType, Boolean expectedControlSuccess) {

        final StreamsBuilder builder = new StreamsBuilder();
        VocabularyView vocabularyView;


        vocabularyView = Mockito.mock(VocabularyView.class);

        when(vocabularyView.get(any(String.class))).thenReturn(1);

        StreamPredicter.create_stream(builder, inputTopic, outputTopic, modelUrl, modelId,labels);

        final TopologyTestDriver streams = new TopologyTestDriver(builder.build(), config);

        ConsumerRecordFactory<String, Sequence> factory = new ConsumerRecordFactory<String, Sequence>(
                new StringSerializer(),
                sequenceSerde.serializer()
        );
        /*Test data*/
        Sequence sequence = new Sequence();
        sequence.call_text = "Buenos dias prueba de llamada mil cuatro";
        sequence.co_verint = "9867384343";
        sequence.co_province = "ES-CO";
        sequence.call_timestamp = new Long(1562893200);
        sequence.duration = 564;
        sequence.province = "Sur-Cordoba";
        sequence.control_type = control_type;
        /*No vamos a llamar al modelo por lo que la longitud da igual*/
        sequence.sequence =  new Integer[] {0, 0};

        /*Powermock Call HTTP*/
        TfModelOutput output = new TfModelOutput();
        output.predictions = new Float [][] {expectedPredictions};
        PowerMockito.mockStatic(CallSeqPredictModel.class);
        when(CallSeqPredictModel.call(any(String.class), any(TfModelInput.class))).thenReturn(output);


        streams.pipeInput(factory.create(inputTopic, sequence.co_verint, sequence));

        ProducerRecord<String, Topic> outputRecord = streams.readOutput(
                outputTopic,
                new StringDeserializer(),
                topicSerde.deserializer()
        );

        Topic t = outputRecord.value();
        logger.info("Sequences: " + Arrays.toString(t.predictions));
        streams.close();
        //OutputVerifier.compareKeyValue(outputRecord, expected.co_verint, expected);
        Assert.assertEquals(outputRecord.key(), sequence.co_verint);
        Assert.assertEquals(t.co_verint, sequence.co_verint);
        Assert.assertEquals(t.co_province, sequence.co_province);
        Assert.assertEquals(t.province, sequence.province);
        Assert.assertEquals(t.call_timestamp, sequence.call_timestamp);
        Assert.assertEquals(t.duration, sequence.duration);
        Assert.assertEquals(t.call_text, sequence.call_text);
        Assert.assertEquals(t.predictions, expectedPredictions);
        Assert.assertEquals(t.pred_type, expectedPredType);
        Assert.assertEquals(t.control_type, sequence.control_type);
        Assert.assertEquals(t.control_success, expectedControlSuccess);

    }
}