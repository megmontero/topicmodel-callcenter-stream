package com.telefonica.topicmodel.nlp;


import org.apache.log4j.Logger;
import org.testng.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class TokenizerTest {
        static final Logger logger = Logger.getLogger(TokenizerTest.class);


    private Object[] testdata1()
    {

        final String orig = "el De La coge movistar cinco factura";
        final String [] expected = new String[] {"coge" ,"movistar" ,"factura"};
        Object []data = new Object[] {orig , expected };
        return data;
    }

    private Object[] testdata2()
    {
        final String orig = "el De La MayúsculA ñus los, mil cinco factura.";
        final String [] expected = new String[] {"mayuscula" ,"nus" ,"factura"};
        Object []data = new Object[] {orig , expected };
        return data;
    }

    @DataProvider(name = "data-provider")
    public Object[][] dataProviderMethod (){

        return new Object[][]{testdata1(), testdata2()};

    }

        @Test(dataProvider = "data-provider")
        public void tokenizerTest1(String orig, String[] expected) {
            final String [] actual = Tokenizer.tokenize(orig);
            Assert.assertEquals(actual, expected);
        }


}
