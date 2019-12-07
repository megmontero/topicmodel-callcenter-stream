package com.telefonica.topicmodel.nlp;


import org.apache.log4j.Logger;
import org.testng.Assert;

import org.testng.annotations.Test;


public class TokenizerTest {
        static final Logger logger = Logger.getLogger(TokenizerTest.class);

        @Test
        public void tokenizerTest1() {
            final String [] expected = {"coge" ,"movistar" ,"factura"};
            final String orig = "el De La coge movistar cinco factura";
            final String [] actual = Tokenizer.tokenize(orig);
            Assert.assertEquals(actual, expected);
        }


}
