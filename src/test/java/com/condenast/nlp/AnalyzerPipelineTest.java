package com.condenast.nlp;

import com.condenast.nlp.opennlp.NERAnalyzer;
import com.condenast.nlp.opennlp.NPExtractorAnalyzer;
import com.condenast.nlp.opennlp.SentenceDetectorAnalyzer;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AnalyzerPipelineTest {

    String testText = "This is a test written by Antonino Rau in New York.";

    @Test
    public void testAssemble() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, NPExtractorAnalyzer.class));
        System.out.println(pipeLine.myTypes());
        assertEquals(8, pipeLine.myTypes().size());
    }

    @Test
    public void testAnalyze() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, NPExtractorAnalyzer.class));
        pipeLine.analyze();
        assertEquals(5, pipeLine.analysis().annotations().size());
        assertEquals(1, pipeLine.analysis().annotations(SentenceDetectorAnalyzer.SENTENCE_TYPE).size());
        assertEquals(1, pipeLine.analysis().annotations("location").size());
        assertEquals(3, pipeLine.analysis().annotations(NPExtractorAnalyzer.NP_ANNOTATION).size());
    }
}