package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.Annotation;
import opennlp.tools.parser.Parse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.condenast.nlp.opennlp.NPExtractorAnalyzer.*;
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
                "Drake, chief of staff for the Reagan Foundation. The Doctors are confident about his recovery.";

        AnalysisContext context = new AnalysisContext(text);
        Analyzer tagExtractorAnalyzer = new NPExtractorAnalyzer(context);
        tagExtractorAnalyzer.analyze();
        assertNotNull(tagExtractorAnalyzer.myAnnotations());
        List<Annotation> npAnnotations = context.annotations(NP_ANNOTATION);
        System.out.println(npAnnotations);
        assertEquals(15, npAnnotations.size());

        Annotation npAnnotation = npAnnotations.get(0);
        assertEquals("Former first lady Nancy Reagan", npAnnotation.text());
        assertNotNull(npAnnotation.getFeature(NP_PARTS));
        List<Parse> npPartsFeature = (List<Parse>) npAnnotation.getFeature(NP_PARTS);
        assertEquals(5, npPartsFeature.size());

        npAnnotation = npAnnotations.get(13);
        assertEquals("Doctors", npAnnotation.text());
        assertNotNull(npAnnotation.getFeature(NP_PARTS));
        npPartsFeature = (List<Parse>) npAnnotation.getFeature(NP_PARTS);
        assertEquals(1, npPartsFeature.size());

        assertIsLemmatized(npAnnotation);

        npAnnotations.forEach(a -> {
            System.out.println(a.text());
            System.out.println(a.getFeature(NP_LEMMATIZED_NGRAMS));
            System.out.println("------\n");
        });

    }

    private void assertIsLemmatized(Annotation npAnnotation) {
        assertEquals("doctor", ((List<Parse>) npAnnotation.getFeature(NP_PARTS)).get(0).getLabel());
    }
}