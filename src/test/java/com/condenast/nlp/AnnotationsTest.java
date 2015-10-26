package com.condenast.nlp;

import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import opennlp.tools.util.Span;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AnnotationsTest {

    @Test
    public void testToBratFormat() throws Exception {
        List<Annotation> annotationList = new ArrayList<>();
        AnalysisContext analysisContext = new AnalysisContext("lorem ipsum data foo bazz lorem ipsum data lorem ipsum data");
        annotationList.add(new Annotation(analysisContext, "TEST", new Span(1, 5), 1.0));
        annotationList.add(new Annotation(analysisContext, "TEST", new Span(10, 15), 1.0));

        Annotation a = new Annotation(analysisContext, ChunksExtractorAnalyzer.NP_ANNOTATION, new Span(1, 20), 1.0);
        List<String> features = Arrays.asList("foo", "baz");
        a.putFeature(ChunksExtractorAnalyzer.LEMMATIZED_NGRAMS_FEATURE, features);
        annotationList.add(a);
        annotationList.add(new Annotation(analysisContext, "TEST", new Span(1, 20), 1.0));
        String actual = Annotations.toBratFormat(annotationList);
        assertNotNull(actual);
        String expected = "T1\tTEST 1 5\torem\n" +
                "#2\tAnnotatorNotes T1\tANNOTATION: type=TEST span=[1..5) prob=1.0 text=orem\n" +
                "T3\tTEST 10 15\tm dat\n" +
                "#4\tAnnotatorNotes T3\tANNOTATION: type=TEST span=[10..15) prob=1.0 text=m dat\n" +
                "T5\tNP_ANNOTATION 1 20\torem ipsum data foo\n" +
                "#6\tAnnotatorNotes T5\tLEMMATIZED_NGRAMS: [foo, baz]\n" +
                "#7\tAnnotatorNotes T5\tANNOTATION: type=NP_ANNOTATION span=[1..20) prob=1.0 text=orem ipsum data foo\n" +
                "T8\tTEST 1 20\torem ipsum data foo\n" +
                "#9\tAnnotatorNotes T8\tANNOTATION: type=TEST span=[1..20) prob=1.0 text=orem ipsum data foo\n";
        assertEquals(expected, actual);
    }
}