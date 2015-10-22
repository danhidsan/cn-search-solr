package com.condenast.nlp.copilot;

import com.condenast.nlp.Annotations;
import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.IOException;

/**
 * Created by arau on 10/20/15.
 */
public class CopilotCorpusAnnotator extends AbstractVisitor {

    private File outAnnotationDir;

    public CopilotCorpusAnnotator(final File outAnnotationDir) {
        this(Integer.MAX_VALUE);
        Validate.notNull(outAnnotationDir);
        this.outAnnotationDir = outAnnotationDir;
    }

    private CopilotCorpusAnnotator(int maxDocs) {
        super(maxDocs);
    }

    @Override
    protected void processDocument(CopilotDocument copilotDocument) {
        CopilotDocumentAnnotator documentAnnotator = CopilodDocumentAnnotatorFactory.buildFor(copilotDocument);
        documentAnnotator.annotate();
        writeAnnotations(documentAnnotator.getAnnotatedCopilotDocument());
    }

    protected void writeAnnotations(AnnotatedCopilotDocument annotatedCopilotDocument) {
        annotatedCopilotDocument.analyses().forEach((analysisName, analysis) -> {
            String uuid = annotatedCopilotDocument.copilotDocument().id();
            File txtFile = new File(outAnnotationDir, txtFileName(analysisName, uuid));
            tryWriteToFile(analysis.text(), txtFile);
            File annFile = new File(outAnnotationDir, annFileName(analysisName, uuid));
            String bratAnnotations = Annotations.toBratFormat(analysis.annotations());
            tryWriteToFile(bratAnnotations, annFile);
        });
    }

    protected String annFileName(String fieldName, String uuid) {
        return String.format("%s-%s.ann", uuid, fieldName);
    }

    protected String txtFileName(String fieldName, String uuid) {
        return String.format("%s-%s.txt", uuid, fieldName);
    }

    private void tryWriteToFile(String text, File txtFile) {
        try {
            FileUtils.writeStringToFile(txtFile, text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void processDocumentSearchSchema(CopilotDocument copilotDocument) {
        // intentionally blank
    }
}
