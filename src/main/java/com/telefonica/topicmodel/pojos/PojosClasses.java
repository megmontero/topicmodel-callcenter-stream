package com.telefonica.topicmodel.pojos;

public class PojosClasses {

    private static class Commons{
        public String call_text;
        public Long call_timestamp;
        public String start_time;
        public String province;
        public String co_province;
        public Integer duration;
        public String co_verint;
    }


    public static class Call extends Commons{
        public Long timestamp;
    }

    public static class Token extends  Commons{
        public String tokens[];
        public Long timestamp;
    }

    public static class Sequence extends  Commons{
        public Integer sequence[];
        public Long timestamp;
    }

     public static class  Topic extends  Commons{
        public Float predictions[];
        public Long timestamp;
        public String error;
    }

    public static class TfModelInput{
        public Integer instances[][];
    }

    public static class TfModelOutput{
        public Float predictions[][];
        public String error;
    }


    public static void copy_commons(Commons a, Commons b)
    {
        a.call_text = b.call_text;
        a.call_timestamp = b.call_timestamp;
        a.call_text = b.call_text;
        a.co_province = b.co_province;
        a.duration = b.duration;
        a.province = b.province;
        a.start_time = b.start_time;
        a.co_verint = b.co_verint;
    }


}
