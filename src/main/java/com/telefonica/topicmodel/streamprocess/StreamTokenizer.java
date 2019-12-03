package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.nlp.Tokenizer;
import com.telefonica.topicmodel.pojos.PojosClasses;
import com.telefonica.topicmodel.serdes.JsonPOJODeserializer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.HashMap;
import java.util.Map;

public class StreamTokenizer {
    private static Serde<PojosClasses.Token> tokenSerde;
    private static  Serde<PojosClasses.Call> callSerde;
    static public void create_stream(final StreamsBuilder builder, final String inputTopic, final String outputTopic)
    {
        initialize_serdes();
        KStream<String, PojosClasses.Call> calls = builder.stream(inputTopic, Consumed.with(Serdes.String(), callSerde));


        KStream<String, PojosClasses.Token> tokens = calls.mapValues(new ValueMapper<PojosClasses.Call, PojosClasses.Token>(){
            @Override
            public PojosClasses.Token apply(PojosClasses.Call value) {
                PojosClasses.Token token = new PojosClasses.Token();
                token.tokens =  Tokenizer.tokenize(value.text);
                token.call_timestamp = value.timestamp;
                token.call_text = value.text;
                return token;
            }
        });

        tokens.to(outputTopic, Produced.with(Serdes.String(), tokenSerde));
    }


    static private void initialize_serdes()
    {
        Map<String, Object> serdeProps = new HashMap<>();
        /*Calls serdes*/
        final Serializer<PojosClasses.Call> callSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Call.class);
        callSerializer.configure(serdeProps, false);

        final Deserializer<PojosClasses.Call> callDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Call.class);
        callDeserializer.configure(serdeProps, false);
        callSerde = Serdes.serdeFrom(callSerializer, callDeserializer);

        /*Tokens Serdes*/
        final Serializer<PojosClasses.Token> tokenSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Token.class);
        tokenSerializer.configure(serdeProps, false);

        final Deserializer<PojosClasses.Token> tokenDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", PojosClasses.Token.class);
        tokenDeserializer.configure(serdeProps, false);
        tokenSerde = Serdes.serdeFrom(tokenSerializer, tokenDeserializer);
    }
}
