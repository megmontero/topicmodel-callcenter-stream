package com.telefonica.topicmodel.model;


import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.POJOClasses.Sequence;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelInput;
import com.telefonica.topicmodel.serdes.POJOClasses.TfModelOutput;
import com.telefonica.topicmodel.serdes.POJOClasses.Topic;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;
import static org.mockito.Matchers.any;



@PrepareForTest(CallSeqPredictModel.class)
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
public class ModelBajaFacturaTest extends PowerMockTestCase {
    static final String modelUrl =  "http://tf-baja-model/v1/models/baja:predict";


    @Test
    public void modelBajaFacturaTest1() {

        Sequence sequence = new Sequence();
        sequence.sequence = new Integer[]{0, 0, 2, 3, 4, 5, 1};
        String expected = "Baja";

        TfModelOutput output = new TfModelOutput();
        output.predictions = new Float[][]{{ new Float(0.6), new Float(0.2), new Float(0.2)}};

        ModelBajaFactura model = new ModelBajaFactura(sequence);

        PowerMockito.mockStatic(CallSeqPredictModel.class);
        Mockito.when(CallSeqPredictModel.call(any(String.class), any(TfModelInput.class))).thenReturn(output);

        Topic topic = model.get_topic();
        //PowerMockito.verifyStatic(CallSeqPredictModel.class, Mockito.times(1));
        Assert.assertEquals(topic.pred_type, expected);


    }

}
