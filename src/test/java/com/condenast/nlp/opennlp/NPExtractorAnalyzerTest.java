package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.Annotation;
import opennlp.tools.parser.Parse;
import org.junit.Before;
import org.junit.Test;

import static com.condenast.nlp.opennlp.NPExtractorAnalyzer.NP_ANNOTATION;
import static com.condenast.nlp.opennlp.NPExtractorAnalyzer.NP_PARTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NPExtractorAnalyzerTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testAnalyze() throws Exception {
        String text = "Former first lady Nancy Reagan was taken to a " +
                "suburban Los Angeles " +
                "hospital \"as a precaution\" Sunday after a " +
                "fall at her home, an " +
                "aide said. " +
                "The 86-year-old Reagan will remain overnight for " +
                "observation at a hospital in Santa Monica, California, " +
                "said Joanne " +
                "Drake, chief of staff for the Reagan Foundation.";

        AnalysisContext context = new AnalysisContext(text);
        Analyzer tagExtractorAnalyzer = new NPExtractorAnalyzer(context);
        tagExtractorAnalyzer.analyze();
        assertNotNull(tagExtractorAnalyzer.myAnnotations());
        assertEquals(13, context.annotations(NP_ANNOTATION).size());
        Annotation npAnnotation = context.annotations(NP_ANNOTATION).get(0);
        assertEquals("Former first lady Nancy Reagan", npAnnotation.text());
        assertNotNull(npAnnotation.getFeature(NP_PARTS));
        assertEquals(5, ((Parse[]) npAnnotation.getFeature(NP_PARTS)).length);
    }
}