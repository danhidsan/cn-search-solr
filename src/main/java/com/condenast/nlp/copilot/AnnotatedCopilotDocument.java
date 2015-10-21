package com.condenast.nlp.copilot;

import com.condenast.nlp.AnalysisContext;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;

import java.util.Map;

/**
 * Created by arau on 10/21/15.
 */
public interface AnnotatedCopilotDocument {
    CopilotDocument copilotDocument();
    Map<String, AnalysisContext> analyses();
    AnalysisContext analysisOf(String body);
}
