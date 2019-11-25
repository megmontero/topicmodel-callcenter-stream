package com.telefonica.topicmodel.pojos;

public class PojosClasses {
    static public class Call {
        public String text;
        public Long timestamp;
    }

    static public class Token {
        public String tokens[];
        public Long timestamp;
    }

    static public class Sequence{
        public Integer sequence[];
        public Long timestamp;
    }

    static public class Topic{
        public Float predictions[];
        public Long timestamp;
    }

    static public class TfModelInput{
        public Integer instances[][];
    }

    static public  class TfModelOutput{
        public Float predictions[][];
    }


}
