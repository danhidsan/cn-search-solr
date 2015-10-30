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
        return new ChunkPartsNGram(chunkParts, minGramSize, maxGramSize).generate();
    }

    static class ChunkPartsNGram {

        private boolean muted = false;
        private List<Parse> chunkParts;
        private int minGramSize;
        private int maxGramSize;
        private int ngramSize = 1;
        private boolean hasNounOrVerb;
        private StringBuilder sb;
        private ArrayList<String> ngrams;

        ChunkPartsNGram(List<Parse> chunkParts, int minGramSize, int maxGramSize) {
            this.minGramSize = minGramSize;
            this.maxGramSize = maxGramSize;
            this.chunkParts = chunkParts;
        }

        public List<String> generate() {
            if (isEmpty(chunkParts) || chunkParts.size() < minGramSize) return Collections.emptyList();
            ngrams = new ArrayList<>();
            for (int startPart = 0; startPart < chunkParts.size(); startPart++) {
                if (chunkParts.size() - startPart < minGramSize) break;
                if (shouldSkipFirstOrLast(chunkParts.get(startPart))) continue;
                sb = new StringBuilder();
                ngramSize = 0;
                hasNounOrVerb = false;
                addToken(startPart);
                for (int currentPart = startPart + 1; currentPart < chunkParts.size(); currentPart++) {
                    addToken(currentPart);
                }
            }
            return ngrams;
        }

        private boolean shouldSkipFirstOrLast(Parse part) {
            if (muted(part)) return true;
            return muted ||
                    part.getType().startsWith("CC") ||
                    part.getType().startsWith("RB") ||
                    part.getType().startsWith("EX") ||
                    part.getType().startsWith("IN");

        }

        private boolean parenthesisStart(Parse part) {
            return part.getCoveredText().startsWith("(") ||
                    part.getCoveredText().startsWith("[") ||
                    part.getCoveredText().startsWith("{") ||
                    part.getType().startsWith("-L");
        }

        private boolean parenthesisEnd(Parse part) {
            return part.getCoveredText().startsWith(")") ||
                    part.getCoveredText().startsWith("]") ||
                    part.getCoveredText().startsWith("}") ||
                    part.getType().startsWith("-R");
        }

        private void addToken(int position) {
            Parse part = chunkParts.get(position);
            if (filter(part)) return;
            sb.append(part.getLabel()).append(" ");
            ngramSize++;
            hasNounOrVerb = hasNounOrVerb || isNounOrVerbPOS(part);
            if (!shouldSkipFirstOrLast(part) && hasNounOrVerb && ngramSize >= minGramSize && ngramSize <= maxGramSize) {
                String ngram = TextHelper.fullTrim(sb.toString());
                if (!ngrams.contains(ngram)) ngrams.add(ngram);
            }
        }

        String filterable = ".,:;\"”“'’?!|()[]{}";

        private boolean filter(Parse part) {
            if (muted(part)) return true;
            return muted || part.getCoveredText().trim().length() <= 2 || filterable.contains(part.getCoveredText().trim().substring(0, 1));
        }

        private boolean muted(Parse part) {
            if (parenthesisStart(part)) {
                muted = true;
            }
            if (parenthesisEnd(part)) {
                muted = false;
                return true;
            }
            return muted;
        }

    }

    private static boolean isNounOrVerbPOS(Parse part) {
        return part.getType().startsWith("N") || part.getType().startsWith("V");
    }

    private static Predicate<String> matchMinNgramSize(int minGramSize) {
        return ngram -> ngram.split(W).length >= minGramSize;
    }

}
