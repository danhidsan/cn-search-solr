package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalyzerPipeline;

import static java.util.Arrays.asList;

/**
 * Created by arau on 10/20/15.
 */
public class DefaultOpenNlpPipeline {

    public static AnalyzerPipeline withText(String text) {
        return AnalyzerPipeline.assemble(text, asList(NERAnalyzer.class, ChunkingAnalyzer.class));
    }

}
