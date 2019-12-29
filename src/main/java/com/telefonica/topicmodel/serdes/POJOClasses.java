package com.telefonica.topicmodel.serdes;

/**
 * POJO Classes needed by microservices.
 */
public class POJOClasses {

    /**
     * Common class with common fields.
     */
    private static class Commons{
        public String call_text;
        public Long call_timestamp;
        public String province;
        public String co_province;
        public Integer duration;
        public String co_verint;
        public String control_type;
    }

    /**
     * Call class for CALLS topic.
     */
    public static class Call extends Commons{
    }

    /**
     * Token Class for TOKENS.CALLS topic.
     */
    public static class Token extends  Commons{
        public String tokens[];
    }

    /**
     * Sequence class for SEQUENCES.CALLS topic.
     */
    public static class Sequence extends  Commons{
        public Integer sequence[];
    }

    /**
     * Topic class for TOPICS.CALLS topic.
     */
     public static class  Topic extends  Commons{
        public Float predictions[];
        public String error;
        public String model;
        public String pred_type;
        public Boolean control_success;
    }

    /**
     * TfModelInput class for tensorflow serving model input.
     */
    public static class TfModelInput{
        public Integer instances[][];
    }

    /**
     * TfModelOutput class for tensorflow serving model output.
     */
    public static class TfModelOutput{
        public Float predictions[][];
        public String error;
    }

    /**
     * Methos for copy common fields between classes
     * @param a  class to get values
     * @param b b class to copy from
     */
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
