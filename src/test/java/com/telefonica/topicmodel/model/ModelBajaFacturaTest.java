package com.telefonica.topicmodel.model;


import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.POJOClasses.Sequence;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelInput;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelOutput;
import com.telefonica.topicmodel.serdes.POJOClasses.Topic;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.any;



@PrepareForTest(CallSeqPredictModel.class)
@PowerMockIgnore({"org.apache.log4j.*"})
public class ModelBajaFacturaTest extends PowerMockTestCase {
    static Config config = ConfigFactory.load();
    static final String modelUrl =  "dummy";
    static final String modelId =  "dummy";
    static final List<String> labels = config.getStringList("modelBajaFactura.modelLabels");

    private Object[] testdata1()
    {
        Float[][] predictions = new Float[][]{{ new Float(0.6), new Float(0.2), new Float(0.2)}};
        String expected = "Baja";
        Object []data = new Object[] {predictions, expected};
        return data;
    }
    private Object[] testdata2()
    {
        Float[][] predictions = new Float[][]{{ new Float(0.1), new Float(0.7), new Float(0.2)}};
        String expected = "Factura";
        Object []data = new Object[] {predictions, expected};
        return data;
    }
    private Object[] testdata3()
    {
        Float[][] predictions = new Float[][]{{ new Float(0.1), new Float(0.4), new Float(0.5)}};
        String expected = "Resto";
        Object []data = new Object[] {predictions, expected};
        return data;
    }


    @DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod (){

        return new Object[][]{testdata1(), testdata2(), testdata3()};

    }

    @Test(dataProvider = "data-provider")
    public void modelBajaFacturaTest1(Float[][] predictions, String expected) {

        Sequence sequence = new Sequence();
        sequence.sequence = new Integer[]{0, 0, 2, 3, 4, 5, 1};

        TfModelOutput output = new TfModelOutput();
        output.predictions = predictions;

        ModelBajaFactura model = new ModelBajaFactura(sequence,modelUrl, modelId, labels);

        PowerMockito.mockStatic(CallSeqPredictModel.class);
        Mockito.when(CallSeqPredictModel.call(any(String.class), any(TfModelInput.class))).thenReturn(output);

        Topic topic = model.get_topic();
        //PowerMockito.verifyStatic(CallSeqPredictModel.class, Mockito.times(1));
        Assert.assertEquals(topic.pred_type, expected);


    }

}
