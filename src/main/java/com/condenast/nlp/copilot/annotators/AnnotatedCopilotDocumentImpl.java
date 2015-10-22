package com.condenast.nlp.copilot.annotators;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.copilot.AnnotatedCopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * Created by arau on 10/20/15.
 */
public class AnnotatedCopilotDocumentImpl implements AnnotatedCopilotDocument {

    private final Map<String, AnalysisContext> fieldNameAnalysisMap = new HashMap<>();

    private final CopilotDocument copilotDocument;

    public AnnotatedCopilotDocumentImpl(CopilotDocument copilotDocument) {
        Validate.notNull(copilotDocument);
        this.copilotDocument = copilotDocument;
    }

    public void addAnalysis(String annotableField, AnalysisContext context) {
        Validate.notEmpty(annotableField);
        Validate.notNull(context);
        fieldNameAnalysisMap.put(annotableField, context);
    }

    @Override
    public CopilotDocument copilotDocument() {
        return copilotDocument;
    }

    @Override
    public Map<String, AnalysisContext> analyses() {
        return unmodifiableMap(fieldNameAnalysisMap);
    }

    @Override
    public AnalysisContext analysisOf(String fieldName) {
        return fieldNameAnalysisMap.get(fieldName);
    }

}
