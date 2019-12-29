package com.telefonica.topicmodel.model;
import com.telefonica.topicmodel.http.CallSeqPredictModel;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
import org.apache.log4j.Logger;
import java.util.List;

/**
 * Class for use Tensoflow Serving Model BajaFactura.
 */
public class ModelBajaFactura {
    private Sequence sequence;
    static final Logger logger = Logger.getLogger(ModelBajaFactura.class);
    private final String modelUrl;
    private final String modelId;
    private final List<String> labels;
    private TfModelInput input;
    private Topic topic;

    /**
     * Constructor of ModelBajaFactura
     * @param s sequence to predict.
     * @param modelUrl URL of Tensoflow Serving service.
     * @param modelId ID of model.
     * @param labels List of labels model classify.
     */
    public ModelBajaFactura(Sequence s,String  modelUrl, String modelId, List<String> labels){
        this.sequence = s;
        input =new TfModelInput();
        input.instances = new Integer[][] {this.sequence.sequence};
        this.modelUrl = modelUrl;
        this.modelId = modelId;
        this.labels = labels;
    }

    /**
     * Do http request to Tensorflow serving model and use output.
     * @return TOPIC POJO class.
     */
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

    /**
     * Format prediction with label.
     */
    private void format_prediction()
    {

        int pred_class_idx =  maximum_index(topic.predictions);
        String true_type;
        topic.pred_type = labels.get(pred_class_idx);
        if (topic.control_type != null){
            true_type = (labels.contains(topic.control_type))?  topic.control_type : "Resto";
            topic.control_success = true_type.equalsIgnoreCase(topic.pred_type);
        }



    }

    /**
     * Method to get index with max value of array.
     * @param a Float array.
     * @return Index of max value.
     */
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
