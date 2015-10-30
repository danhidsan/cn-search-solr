package com.condenast.nlp;

import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
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
        assertEquals(expectedNgrams.stream().sorted().collect(toList()), actualNgrams.stream().sorted().collect(toList()));
    }

    @Test
    public void testGenerateNGramsFromChunks() throws Exception {
        AnalysisContext context = analyze("Ciccio's super-mega fancy hotels rooms are fantastic", 2, 3);
        assertExpectedFirstNpNgramsAre(context, "Ciccio's super-mega", "Ciccio's super-mega fancy", "super-mega fancy hotel", "fancy hotel", "fancy hotel room", "hotel room");
    }

    @Test
    public void testGenerateNGramsFromChunks_ignoreCC_Start_Or_End() throws Exception {
        AnalysisContext context = analyze("Ciccio and Mario are good guys.", 2, 3);
        assertExpectedFirstNpNgramsAre(context, "Ciccio and Mario");
        context = analyze("Ciccio and are good guys.", 1, 3);
        assertExpectedFirstNpNgramsAre(context, "Ciccio");
    }

    @Test
    public void testGenerateNGramsFromChunks_ignoreParenthesis() throws Exception {
        AnalysisContext context = analyze("Ciccio and (Mario) and Paolo are good guys.", 1, 3);
        assertExpectedFirstNpNgramsAre(context, "Ciccio", "Paolo");
    }

    @Test
    public void testGenerateNGramsFromChunks_ignoreParenthesis_2() throws Exception {
        AnalysisContext context = analyze("Those were qualities their previous L.A. abode—a sleek jewel box perched " + "on the 20th floor of a residential tower (*Architectural Digest,* December 2009)—simply did not offer.", 1, 3);
        assertNotNull(context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION));
        Annotation annotation = context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION).get(2);
        List<String> actualNgrams = (List<String>) annotation.getFeature(ChunksExtractorAnalyzer.LEMMATIZED_NGRAMS_FEATURE);
        assertNgrams(asList("residential tower"), actualNgrams);
    }

    @Test
    public void testGenerateNGramsFromChunks_filterPunctuation() throws Exception {
        AnalysisContext context = analyze("“I remember walking to the front door and saying, ‘It’s perfect.’” “And the one-story thing is so great for Zachary,” John adds.", 1, 3);
        assertNotNull(context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION));
        Annotation annotation = context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION).get(4);
        List<String> actualNgrams = (List<String>) annotation.getFeature(ChunksExtractorAnalyzer.LEMMATIZED_NGRAMS_FEATURE);
        assertNgrams(asList("John"), actualNgrams);
    }

    private void assertExpectedFirstNpNgramsAre(AnalysisContext context, String... expectedNGrams) {
        assertNotNull(context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION));
        Annotation annotation = context.annotations(ChunksExtractorAnalyzer.NP_ANNOTATION).get(0);
        List<String> actualNgrams = (List<String>) annotation.getFeature(ChunksExtractorAnalyzer.LEMMATIZED_NGRAMS_FEATURE);
        assertNgrams(asList(expectedNGrams), actualNgrams);
    }

    private AnalysisContext analyze(String text, int min, int max) {
        AnalysisContext context = new AnalysisContext(text);
        ChunksExtractorAnalyzer chunksExtractorAnalyzer = new ChunksExtractorAnalyzer(context);
        chunksExtractorAnalyzer.setMinNGramsSize(min);
        chunksExtractorAnalyzer.setMaxNGramsSize(max);
        chunksExtractorAnalyzer.analyze();
        return context;
    }

}