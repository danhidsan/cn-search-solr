package com.condenast.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/19/15.
 */
public class NGramsHelper {


    private static final String W = "[\\W+]";

    public static List<String> generate(String str, int minGramSize, int maxGramSize) {

        List<String> sentence = Arrays.asList(str.split(W));

        List<String> ngrams = new ArrayList<>();
        int ngramSize = 0;
        StringBuilder sb = null;

        //sentence becomes ngrams
        for (ListIterator<String> it = sentence.listIterator(); it.hasNext(); ) {
            String word = it.next();

            //1- add the word itself
            sb = new StringBuilder(word);
            ngrams.add(word);
            ngramSize = 1;
            it.previous();

            //2- insert prevs of the word and add those too
            while (it.hasPrevious() && ngramSize < maxGramSize) {
                sb.insert(0, ' ');
                sb.insert(0, it.previous());
                ngrams.add(sb.toString());
                ngramSize++;
            }

            //go back to initial position
            while (ngramSize > 0) {
                ngramSize--;
                it.next();
            }
        }
        return ngrams.stream().filter(matchMinNgramSize(minGramSize)).map(cleanText()).collect(toList());
    }

    private static Predicate<String> matchMinNgramSize(int minGramSize) {
        return ngram -> ngram.split(W).length >= minGramSize;
    }

    private static Function<String, String> cleanText() {
        return s -> s.trim().replaceAll("\\s+", " ");
    }


}
