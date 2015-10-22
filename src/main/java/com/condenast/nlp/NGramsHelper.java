package com.condenast.nlp;

import opennlp.tools.parser.Parse;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Created by arau on 10/19/15.
 */
public class NGramsHelper {

    private static final String W = " ";

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
        return ngrams.stream().filter(matchMinNgramSize(minGramSize)).map(TextHelper::fullTrim).collect(toList());
    }

    public static List<String> generateNGramsFromChunking(List<Parse> chunkParts, int minGramSize, int maxGramSize) {
        if (isEmpty(chunkParts) || chunkParts.size() < minGramSize) return Collections.emptyList();
        List<String> ngrams = new ArrayList<>();
        for (int head = 0; head < chunkParts.size(); head++) {
            if (chunkParts.size() - head < minGramSize) break;
            if (chunkParts.size() - head > maxGramSize) continue;
            StringBuilder sb = new StringBuilder();
            boolean hasNounOrVerb = addToken(chunkParts, head, sb);
            for (int current = head + 1; current < chunkParts.size(); current++) {
                hasNounOrVerb = addToken(chunkParts, current, sb) || hasNounOrVerb;
            }
            if (hasNounOrVerb) ngrams.add(TextHelper.fullTrim(sb.toString()));
        }
        return ngrams;
    }

    private static boolean addToken(List<Parse> chunkParts, int pos, StringBuilder sb) {
        Parse part = chunkParts.get(pos);
        sb.append(part.getLabel()).append(" ");
        return isNounOrVerbPOS(part);
    }

    private static boolean isNounOrVerbPOS(Parse part) {
        return part.getType().startsWith("N") || part.getType().startsWith("V");
    }

    private static Predicate<String> matchMinNgramSize(int minGramSize) {
        return ngram -> ngram.split(W).length >= minGramSize;
    }

}
