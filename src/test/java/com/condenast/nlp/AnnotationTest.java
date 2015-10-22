package com.condenast.nlp;

import opennlp.tools.util.Span;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class AnnotationTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testToBratFormat() throws Exception {

        AnalysisContext analysisContext = new AnalysisContext("lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum");
        Annotation annotation = new Annotation(analysisContext, "TEST", new Span(10, 20), 1.0);
        String actual = annotation.toBratFormat(new AtomicInteger(1));
        String expected = "T1\tTEST 10 20\tm lorem ip";
        Assert.assertEquals(expected, actual);

    }
}