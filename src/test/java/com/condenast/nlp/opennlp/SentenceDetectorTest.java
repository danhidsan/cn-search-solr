package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Annotation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SentenceDetectorTest {

    private SentenceDetector sentenceDetector;

    @Before
    public void setUp() throws Exception {
        String text = "This is a sentence. This is another sentence.";
        sentenceDetector = new SentenceDetector(new AnalysisContext(text));
    }

    @Test
    public void testSentenceDetector() {
        sentenceDetector.analyze();
        List<Annotation> annotationList = sentenceDetector.myAnnotations();
        assertNotNull(annotationList);
        assertEquals(2, annotationList.size());

        assertEquals("This is a sentence.", annotationList.get(0).text());
        assertEquals("This is another sentence.", annotationList.get(1).text());
    }
}