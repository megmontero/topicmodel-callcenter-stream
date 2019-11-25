package com.telefonica.topicmodel;




import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.nlp.Tokenizer;
import com.telefonica.topicmodel.pojos.PojosClasses.*;
import com.telefonica.topicmodel.serdes.JsonPOJODeserializer;
import com.telefonica.topicmodel.serdes.JsonPOJOSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.util.*;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class MGM {





    static final String inputTopic = "CALLS";
    static final String outputTopic = "TOKENS.CALLS";
    static final Logger logger = Logger.getLogger(MGM.class);
    public static void main(String[] args) {
        BasicConfigurator.configure();
        final StreamConfig streamsConfiguration = new StreamConfig("MGM");
        final StreamsBuilder builder = new StreamsBuilder();
        createWordCountStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration.get_properties());
        streams.cleanUp();

        // Now run the processing topology via `start()` to begin processing its input data.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }



    static void createWordCountStream(final StreamsBuilder builder) {
        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).  The default key and value serdes will be used.

        Map<String, Object> serdeProps = new HashMap<>();
        /*Calls serdes*/
        final Serializer<Call> callSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Call.class);
        callSerializer.configure(serdeProps, false);

        final Deserializer<Call> callDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Call.class);
        callDeserializer.configure(serdeProps, false);
        final Serde<Call> callSerde = Serdes.serdeFrom(callSerializer, callDeserializer);

        /*Tokens Serdes*/
        final Serializer<Token> tokenSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Token.class);
        tokenSerializer.configure(serdeProps, false);

        final Deserializer<Token> tokenDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Token.class);
        tokenDeserializer.configure(serdeProps, false);
        final Serde<Token> tokenSerde = Serdes.serdeFrom(tokenSerializer, tokenDeserializer);




        KStream<String, Call> calls = builder.stream(inputTopic, Consumed.with(Serdes.String(), callSerde));


        KStream<String, Token> tokens = calls.mapValues(new ValueMapper<Call, Token> (){
            @Override
            public Token apply(Call value) {
                Token token = new Token();
                token.tokens =  Tokenizer.tokenize(value.text);
                return token;
            }
        });

        tokens.to(outputTopic, Produced.with(Serdes.String(), tokenSerde));


                //(Serdes.String()outputTopic, Produced.with(Serdes.String(), tokenSerde));

        //final KStream<String, String> textLines = builder.stream(inputTopic);

        //final Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);

        /*final KTable<String, Long> wordCounts = textLines
                // Split each text line, by whitespace, into words.  The text lines are the record
                // values, i.e. we can ignore whatever data is in the record keys and thus invoke
                // `flatMapValues()` instead of the more generic `flatMap()`.
                .flatMapValues(value -> Arrays.asList(pattern.split(value.toLowerCase())))
                // Group the split data by word so that we can subsequently count the occurrences per word.
                // This step re-keys (re-partitions) the input data, with the new record key being the words.
                // Note: No need to specify explicit serdes because the resulting key and value types
                // (String and String) match the application's default serdes.
                .groupBy((keyIgnored, word) -> word)
                // Count the occurrences of each word (record key).
                .count();
        */

        //final KTable<String, String> wordCounts = textLines.mapValues(v -> v.toUpperCase());


        // Write the `KTable<String, Long>` to the output topic.
        //wordCounts.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
    }

}