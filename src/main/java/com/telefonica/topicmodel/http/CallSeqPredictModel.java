package com.telefonica.topicmodel.http;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telefonica.topicmodel.PredicterLauncher;
import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.pojos.PojosClasses.*;
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

        try {
            String json = mapper.writeValueAsString(input);
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            ObjectMapper mapper2 = new ObjectMapper();
            CloseableHttpResponse response = client.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            output = mapper.readValue(result, PojosClasses.TfModelOutput.class);
            logger.debug(output.predictions[0][0]);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
        }
        return output;
    }

}
