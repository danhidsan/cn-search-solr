package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.Annotation;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NERAnalyzerTest {

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
        Analyzer nerOpenNLP = new NERAnalyzer(context, asList("person", "location", "date"));
        nerOpenNLP.analyze();
        List<Annotation> annotations = nerOpenNLP.myAnnotations();
        assertNotNull(nerOpenNLP.myAnnotations());
        System.out.println(nerOpenNLP.myAnnotations());
        assertEquals(4, context.annotations("person").size());
        assertEquals("Nancy Reagan", context.annotations("person").get(0).text());
        assertEquals(3, context.annotations("location").size());
        assertEquals("Los Angeles", context.annotations("location").get(0).text());
        assertEquals(1, context.annotations("date").size());

        text = "Ciccio is the CEO of Microsoft.";
        context = new AnalysisContext(text);
        nerOpenNLP = new NERAnalyzer(context, asList("person", "location", "date", "organization"));
        nerOpenNLP.analyze();
        assertNotNull(nerOpenNLP.myAnnotations());
        System.out.println(nerOpenNLP.myAnnotations());
        assertEquals(1, context.annotations("organization").size());

    }
}