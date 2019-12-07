package com.telefonica.topicmodel.model;
import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class ModelBajaFactura {
    private Sequence sequence;
    static final Logger logger = Logger.getLogger(ModelBajaFactura.class);
    //static final String modelUrl = "http://tf-baja-model:8501/v1/models/baja:predict" ;//"http://tf-baja-model/v1/models/baja:predict";
    static final String modelUrl =  "http://tf-baja-model/v1/models/baja:predict";
    static final String modelId = "bajafactura";
    static final String [] labels = {"Baja", "Factura", "Resto"};
    private TfModelInput input;
    private Topic topic;
    public ModelBajaFactura(Sequence s){
        this.sequence = s;
        input =new TfModelInput();
        input.instances = new Integer[][] {this.sequence.sequence};
    }

    public Topic get_topic()
    {
        topic = new Topic();
        topic.model = modelId;
        POJOClasses.copy_commons(topic, sequence);
        TfModelOutput output = CallSeqPredictModel.call(modelUrl, input);
        if (output.error== null)
            if (output.predictions!= null && output.predictions.length >0) {
                topic.predictions = output.predictions[0];
                format_prediction();
            }
            else
                logger.error("HTTP error or not predictions");
        else
            topic.error = output.error;


        return topic;
    }


    private void format_prediction()
    {

        int pred_class_idx =  maximum_index(topic.predictions);
        String true_type;
        topic.pred_type = labels[pred_class_idx];
        List<String> model_labels = Arrays.asList(labels);
        if (topic.control_type != null){
            true_type = (model_labels.contains(topic.control_type))?  topic.control_type : "Resto";
            topic.control_success = true_type.equalsIgnoreCase(topic.pred_type);
        }



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
