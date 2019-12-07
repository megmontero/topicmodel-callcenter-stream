package com.telefonica.topicmodel.nlp;

import java.text.Normalizer;
import java.util.Arrays;

public class  Tokenizer {

    private static final String[] diacritical = new String [] {"[\\p{InCombiningDiacriticalMarks}]", ""};
    private static final String[] empties = new String [] {"[\\n\\t]", " "};
    private static final String[] specials = new String [] {"\\W", " "};
    private static final String[] multispace = new String[] {" +", " "};
    private static final String[] o2 = new String[] {" o dos ", " o2 "};

    private static final String patterns[][] = new String [][] {diacritical,empties, specials, o2, multispace};

    static public String[] tokenize(String str)
    {
        String[] tokens;
        str = str.toLowerCase();
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        for (String[] pattern : patterns) {
            str = str.replaceAll(pattern[0], pattern[1]);
        }
        tokens =  Arrays.stream(str.split(" ")).filter(x -> !CommonWords.allCommons.contains(x) && x.length() > 2).toArray(String[]::new);

        return tokens;
    }






}
