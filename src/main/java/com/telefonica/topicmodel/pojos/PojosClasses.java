package com.telefonica.topicmodel.pojos;

public class PojosClasses {
    static public class Call {
        public String text;
        public Long timestamp;
        public String start_time;
        public String province;
        public String co_province;
        public Integer duration;
    }

    static public class Token {
        public String call_text;
        public String tokens[];
        public Long timestamp;
        public Long call_timestamp;
        public String start_time;
        public String province;
        public String co_province;
        public Integer duration;
    }

    static public class Sequence{
        public String call_text;
        public Integer sequence[];
        public Long timestamp;
        public Long call_timestamp;
        public String start_time;
        public String province;
        public String co_province;
        public Integer duration;
    }

    static public class Topic{
        public String call_text;
        public Float predictions[];
        public Long timestamp;
        public Long call_timestamp;
        public String error;
        public String start_time;
        public String province;
        public String co_province;
        public Integer duration;
    }

    static public class TfModelInput{
        public Integer instances[][];
    }

    static public  class TfModelOutput{
        public Float predictions[][];
        public String error;
    }


}
