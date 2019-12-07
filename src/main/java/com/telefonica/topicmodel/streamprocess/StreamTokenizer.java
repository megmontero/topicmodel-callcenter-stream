package com.telefonica.topicmodel.streamprocess;

import com.telefonica.topicmodel.nlp.Tokenizer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerdes;
import com.telefonica.topicmodel.serdes.POJOClasses;
import com.telefonica.topicmodel.serdes.POJOClasses.*;
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
    private static Serde<Token> tokenSerde;
    private static  Serde<Call> callSerde;
    static public void create_stream(final StreamsBuilder builder, final String inputTopic, final String outputTopic)
    {
        tokenSerde = JsonPOJOSerdes.getObjectSerde(Token.class);
        callSerde = JsonPOJOSerdes.getObjectSerde(Call.class);
        KStream<String, Call> calls = builder.stream(inputTopic, Consumed.with(Serdes.String(), callSerde));


        KStream<String, Token> tokens = calls.mapValues(value -> {
            Token token = new Token();
            token.tokens =  Tokenizer.tokenize(value.call_text);
            POJOClasses.copy_commons(token, value);
            return token;
        });

        tokens.to(outputTopic, Produced.with(Serdes.String(), tokenSerde));
    }


}
