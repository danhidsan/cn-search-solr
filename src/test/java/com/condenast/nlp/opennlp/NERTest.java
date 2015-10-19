package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NERTest {

    @Test
    public void testFind() throws Exception {

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
        Analyzer nerOpenNLP = new NER(context);
        nerOpenNLP.analyze();
        assertNotNull(nerOpenNLP.myAnnotations());
        System.out.println(nerOpenNLP.myAnnotations());
        assertEquals(4, context.annotations("person").size());
        assertEquals("Nancy Reagan", context.annotations("person").get(0).text());
        assertEquals(3, context.annotations("location").size());
        assertEquals("Los Angeles", context.annotations("location").get(0).text());
        assertEquals(1, context.annotations("date").size());
    }
}