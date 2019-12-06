package com.telefonica.topicmodel.serdes;

public class PojosClasses {

    private static class Commons{
        public String call_text;
        public Long call_timestamp;
        public String province;
        public String co_province;
        public Integer duration;
        public String co_verint;
        public String control_type;
    }


    public static class Call extends Commons{
    }

    public static class Token extends  Commons{
        public String tokens[];
    }

    public static class Sequence extends  Commons{
        public Integer sequence[];
    }

     public static class  Topic extends  Commons{
        public Float predictions[];
        public String error;
        public String model;
        public String pred_type;
        public Boolean control_success;
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
        a.control_type = b.control_type;
        a.co_verint = b.co_verint;
    }


}
