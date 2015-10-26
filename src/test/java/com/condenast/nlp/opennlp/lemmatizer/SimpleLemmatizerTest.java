package com.condenast.nlp.opennlp.lemmatizer;

import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import com.condenast.nlp.opennlp.ResourceUtil;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class SimpleLemmatizerTest {

    @Test
    public void testLemmatize() throws Exception {
        SimpleLemmatizer lemmatizer = new SimpleLemmatizer();
        String tokens[] = new String[]{"Most", "large", "cities", "in", "the", "US", "had", "morning", "and", "afternoon", "newspapers", "."};
        POSTaggerME tagger = new POSTaggerME(ResourceUtil.modelOf(ChunksExtractorAnalyzer.EN_POS_MAXENT_MODEL_FILENAME, POSModel.class));
        String posTags[] = tagger.tag(tokens);
        List<String> lemmas = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            lemmas.add(lemmatizer.lemmatize(tokens[i], posTags[i]));
        }
        List<String> expectedLemmas = asList("much", "large", "city", "in", "the", "US", "have", "morning", "and", "afternoon", "newspaper", ".");
        AtomicInteger idx = new AtomicInteger();
        expectedLemmas.forEach(expectedLemma -> assertEquals(expectedLemma, lemmas.get(idx.getAndIncrement())));
    }


//    @Test
//    public void testLemmatizeWithPunctAtTheEnd() throws Exception {
//        SimpleLemmatizer lemmatizer = new SimpleLemmatizer();
//        String tokens[] = new String[]{"It", "is", "cold", "in ", "the", "cities."};
//        POSTaggerME tagger = new POSTaggerME(ResourceUtil.modelOf(ChunksExtractorAnalyzer.EN_POS_MAXENT_MODEL_FILENAME, POSModel.class));
//        String posTags[] = tagger.tag(tokens);
//        System.out.println(asList(posTags));
//        List<String> lemmas = new ArrayList<>();
//        for (int i = 0; i < tokens.length; i++) {
//            lemmas.add(lemmatizer.lemmatize(tokens[i], posTags[i]));
//        }
//        System.out.println(lemmas);
//        List<String> expectedLemmas = asList("city");
//        AtomicInteger idx = new AtomicInteger();
//        expectedLemmas.forEach(expectedLemma -> assertEquals(expectedLemma, lemmas.get(idx.getAndIncrement())));
//    }
}