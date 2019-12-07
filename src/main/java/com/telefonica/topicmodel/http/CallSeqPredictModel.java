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

public class CallSeqPredictModel {
    static final Logger logger = Logger.getLogger(CallSeqPredictModel.class);

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
                logger.error("return error peticion: " + json );
                logger.error("return error respuesta: "+ result);
            }


        }
        catch (Exception e)
        {
            //logger.error(e.getMessage());
            logger.error("peticion: " + json );
            logger.error("respuesta: "+ result);
        }
        return output;
    }

}
