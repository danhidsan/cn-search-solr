package com.condenast.nlp.copilot;

import com.condenast.nlp.copilot.annotators.AnnotatedCopilotDocumentImpl;
import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;

import java.io.File;

/**
 * Created by arau on 10/20/15.
 */
public class CopilotCorpusAnnotator extends AbstractVisitor {

    private File outAnnotationDir;

    public CopilotCorpusAnnotator(final File outAnnotationDir) {
        this(Integer.MAX_VALUE);
        this.outAnnotationDir = outAnnotationDir;
    }

    private CopilotCorpusAnnotator(int maxDocs) {
        super(maxDocs);
    }

    @Override
    protected void processDocument(CopilotDocument copilotDocument) {
        CopilotDocumentAnnotator documentAnnotator = CopilodDocumentAnnotatorFactory.buildFor(copilotDocument);
        documentAnnotator.annotate();
        //writeAnnotatedDoc(annotatedCopilotDocumentImpl, outAnnotationDir);
    }

    private void writeAnnotatedDoc(AnnotatedCopilotDocumentImpl annotatedCopilotDocumentImpl, File outAnnotationDir) {

    }

    @Override
    protected void processDocumentSearchSchema(CopilotDocument copilotDocument) {
    }
}
