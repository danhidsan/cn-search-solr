package com.condenast.nlp;

import com.condenast.nlp.opennlp.NERAnalyzer;
import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import com.condenast.nlp.opennlp.SentenceDetectorAnalyzer;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AnalyzerPipelineTest {

    String testText = "This is a test written by Antonino Rau in New York.";

    @Test
    public void testAssemble() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, ChunksExtractorAnalyzer.class));
        System.out.println(pipeLine.myTypes());
        assertEquals(5, pipeLine.myTypes().size());
    }

    @Test
    public void testAnalyze() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, ChunksExtractorAnalyzer.class));
        pipeLine.analyze();
        assertEquals(10, pipeLine.analysis().annotations().size());
        assertEquals(1, pipeLine.analysis().annotations(SentenceDetectorAnalyzer.SENTENCE_ANNOTATION).size());
        assertEquals(1, pipeLine.analysis().annotations("location").size());
        assertEquals(3, pipeLine.analysis().annotations(ChunksExtractorAnalyzer.NP_ANNOTATION).size());
        assertEquals(2, pipeLine.analysis().annotations(ChunksExtractorAnalyzer.VP_ANNOTATION).size());
    }
}