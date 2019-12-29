package com.telefonica.topicmodel.http;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Class to do a request to Tensorflow serving model.
 */
public class CallSeqPredictModel {
    static final Logger logger = Logger.getLogger(CallSeqPredictModel.class);

    /**
     * Do a request to tensorflow serving model and return result.
     * @param url URL of tensorflow serving service.
     * @param input Input with sequence.
     * @return Prediction of Tensorflow Serving model.
     */
    public static TfModelOutput call(String url, TfModelInput input)
    {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        TfModelOutput output = new TfModelOutput();
        output.predictions = new Float[][]{};
        ObjectMapper mapper = new ObjectMapper();
        String json="", result="";
        try {
            json = mapper.writeValueAsString(input);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = client.execute(httpPost);
            result = EntityUtils.toString(response.getEntity());
            output = mapper.readValue(result, POJOClasses.TfModelOutput.class);
            if (output.error != null){
                logger.error("Http return error peticion: " + json );
                logger.error("Http return error respuesta: "+ result);
            }


        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            logger.error("Http request:  " + json );
            logger.error("Http Response: "+ result);
        }
        return output;
    }

}
