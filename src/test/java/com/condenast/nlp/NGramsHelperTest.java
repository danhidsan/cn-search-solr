package com.condenast.nlp;

import com.condenast.nlp.opennlp.ChunkingAnalyzer;
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
        ChunkingAnalyzer chunkingAnalyzer = new ChunkingAnalyzer(context);
        chunkingAnalyzer.analyze();

        assertNotNull(context.annotations(ChunkingAnalyzer.NP_ANNOTATION));

        Annotation annotation = context.annotations(ChunkingAnalyzer.NP_ANNOTATION).get(0);
        List<String> actualNgrams = (List<String>) annotation.getFeature(ChunkingAnalyzer.LEMMATIZED_NGRAMS_FEATURE);
        System.out.println(actualNgrams);

        List<String> expectedNGrams = asList("Ciccio's super-mega", "Ciccio's super-mega fancy", "Ciccio's super-mega" + " fancy hotel", "Ciccio's super-mega fancy hotel room", "super-mega fancy", "super-mega fancy hotel", "super-mega fancy hotel room", "fancy hotel", "fancy hotel room", "hotel room");

        assertNgrams(expectedNGrams, actualNgrams);
    }

}