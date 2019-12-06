package com.telefonica.topicmodel.model;
import com.telefonica.topicmodel.PredicterLauncher;
import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.PojosClasses;
import com.telefonica.topicmodel.serdes.PojosClasses.*;
import org.apache.log4j.Logger;

public class ModelBajaFactura {
    Sequence sequence;
    static final Logger logger = Logger.getLogger(ModelBajaFactura.class);
    static final String modelUrl = "http://tf-baja-model:8501/v1/models/baja:predict" ;//"http://tf-baja-model/v1/models/baja:predict";
    //static final String modelUrl =  "http://tf-baja-model/v1/models/baja:predict";

    static final String [] labels = {"Baja", "Factura", "Resto"};
    private TfModelInput input;
    private Topic topic;
    public ModelBajaFactura(Sequence s){
        this.sequence = s;
        TfModelInput input =new TfModelInput();
        input.instances = new Integer[][] {sequence.sequence};
    }

    public Topic get_topic()
    {
        topic = new Topic();

        TfModelOutput output = CallSeqPredictModel.call(modelUrl, input);
        if (output.error== null)
            if (output.predictions!= null && output.predictions.length >0) {
                topic.predictions = output.predictions[0];
                format_prediction();
            }
            else
                logger.error("HTTP errror or not predictions");
        else
            topic.error = output.error;
        PojosClasses.copy_commons(topic, sequence);

        return topic;
    }


    private void format_prediction()
    {

        int pred_class_idx =  maximum_index(topic.predictions);
        String pred_class = labels[pred_class_idx];
        logger.error("Debug: " + pred_class);


    }
    private int maximum_index(Float a[])
    {
        Float maximum;
        int index=0,i=1;
        maximum=a[0];
        while(i<a.length)
        {
            if(maximum<a[i])
            {
                maximum=a[i];
                index=i;
            }
            i++;
        }
        return index;
    }
}
