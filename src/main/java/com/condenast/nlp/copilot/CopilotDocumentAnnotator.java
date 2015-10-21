package com.condenast.nlp.copilot;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;

/**
 * Created by arau on 10/20/15.
 */
public interface CopilotDocumentAnnotator {

    void annotate();

    void setCopilotDocument(CopilotDocument copilotDocument);

    AnnotatedCopilotDocument getAnnotatedCopilotDocument();
}
