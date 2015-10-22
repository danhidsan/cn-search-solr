package com.condenast.nlp;

import com.condenast.nlp.opennlp.NERAnalyzer;
import com.condenast.nlp.opennlp.ChunkingAnalyzer;
import com.condenast.nlp.opennlp.SentenceDetectorAnalyzer;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AnalyzerPipelineTest {

    String testText = "This is a test written by Antonino Rau in New York.";

    @Test
    public void testAssemble() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, ChunkingAnalyzer.class));
        System.out.println(pipeLine.myTypes());
        assertEquals(5, pipeLine.myTypes().size());
    }

    @Test
    public void testAnalyze() throws Exception {
        AnalyzerPipeline pipeLine = AnalyzerPipeline.assemble(testText, asList(NERAnalyzer.class, ChunkingAnalyzer.class));
        pipeLine.analyze();
        assertEquals(7, pipeLine.analysis().annotations().size());
        assertEquals(1, pipeLine.analysis().annotations(SentenceDetectorAnalyzer.SENTENCE_ANNOTATION).size());
        assertEquals(1, pipeLine.analysis().annotations("location").size());
        assertEquals(3, pipeLine.analysis().annotations(ChunkingAnalyzer.NP_ANNOTATION).size());
        assertEquals(2, pipeLine.analysis().annotations(ChunkingAnalyzer.VP_ANNOTATION).size());
    }
}