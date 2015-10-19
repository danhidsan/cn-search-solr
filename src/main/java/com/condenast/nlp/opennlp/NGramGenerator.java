package com.condenast.nlp.opennlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by arau on 10/19/15.
 */
public class NGramGenerator {

    public static List<String> generate(String str, int minGramSize, int maxGramSize) {

        List<String> sentence = Arrays.asList(str.split("[\\W+]"));

        List<String> ngrams = new ArrayList<>();
        int ngramSize = 0;
        StringBuilder sb = null;

        for (ListIterator<String> it = sentence.listIterator(); it.hasNext(); ) {
            String word = it.next();

            sb = new StringBuilder(word);
            if (minGramSize <= 1) ngrams.add(word);
            ngramSize = 1;
            it.previous();

            while (it.hasPrevious() && ngramSize <= maxGramSize) {
                sb.insert(0, ' ');
                sb.insert(0, it.previous());
                if (ngramSize <= maxGramSize && ngramSize >= minGramSize) ngrams.add(sb.toString());
                ngramSize++;
            }

            while (ngramSize > 0) {
                ngramSize--;
                it.next();
            }
        }
        return ngrams;
    }


}
