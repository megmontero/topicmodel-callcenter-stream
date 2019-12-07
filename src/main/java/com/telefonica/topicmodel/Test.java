package com.telefonica.topicmodel;

import com.telefonica.topicmodel.config.StreamConfig;
import com.telefonica.topicmodel.streamprocess.StreamSequencer;
import com.telefonica.topicmodel.view.VocabularyView;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Test {


        final String outputTopic = "SEQUENCES.CALLS";
        final String vocabularyTopic = "TBL.VOCABULARY.CALLS";
        final String applicationId = "topic.model.sequencer";

        static final Logger logger = Logger.getLogger(com.telefonica.topicmodel.Test.class);


        public static void main(String[] args) {
                Config config = ConfigFactory.load();
                final String inputTopic = config.getString("tokenizer.inputTopic"); //"TOKENS.CALLS";
                logger.error("DEBUG: " + inputTopic);



        }




}
