package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Annotation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SentenceDetectorAnalyzerTest {

    private SentenceDetectorAnalyzer sentenceDetectorAnalyzer;

    @Before
    public void setUp() throws Exception {
        String text = "This is a sentence. This is another sentence.";
        sentenceDetectorAnalyzer = new SentenceDetectorAnalyzer(new AnalysisContext(text));
    }

    @Test
    public void testAnalyze() {
        sentenceDetectorAnalyzer.analyze();
        List<Annotation> annotationList = sentenceDetectorAnalyzer.myAnnotations();
        assertNotNull(annotationList);
        assertEquals(2, annotationList.size());

        assertEquals("This is a sentence.", annotationList.get(0).text());
        assertEquals("This is another sentence.", annotationList.get(1).text());
    }
}