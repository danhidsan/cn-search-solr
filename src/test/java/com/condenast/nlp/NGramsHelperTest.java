package com.condenast.nlp;

import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NGramsHelperTest {

    @Test
    public void testGenerateTriGramFromString() throws Exception {
        String test = "former first lady Nancy Reagan";
        List<String> actualNgrams = NGramsHelper.generate(test, 3, Integer.MAX_VALUE);
        assertEquals(6, actualNgrams.size());
        List<String> expectedNgrams = asList("former first lady", "first lady Nancy", "former first lady Nancy", "lady Nancy Reagan", "first lady Nancy Reagan", "former first lady Nancy Reagan");
        assertNgrams(expectedNgrams, actualNgrams);
    }

    private void assertNgrams(List<String> expectedNgrams, List<String> actualNgrams) {
        AtomicInteger idx = new AtomicInteger();
        expectedNgrams.forEach(expectedNgram -> assertEquals(expectedNgram, actualNgrams.get(idx.getAndIncrement())));
    }

    @Test
    public void testGenerateNGramsFromChunks() throws Exception {

        AnalysisContext context = new AnalysisContext("Ciccio's super-mega fancy hotels rooms are fantastic");
        ChunksExtractorAnalyzer chunksExtractorAnalyzer = new ChunksExtractorAnalyzer(context);
        chunksExtractorAnalyzer.setMinNGramsSize(2);
        chunksExtractorAnalyzer.setMaxNGramsSize(3);
        chunksExtractorAnalyzer.analyze();

        assertNotNull(context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION));

        Annotation annotation = context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION).get(0);
        List<String> actualNgrams = (List<String>) annotation.getFeature(ChunksExtractorAnalyzer.LEMMATIZED_NGRAMS_FEATURE);
        System.out.println(actualNgrams);

        List<String> expectedNGrams = asList("Ciccio's super-mega", "Ciccio's super-mega fancy", "super-mega fancy hotel", "fancy hotel", "fancy hotel room", "hotel room");

        assertNgrams(expectedNGrams, actualNgrams);
    }

}