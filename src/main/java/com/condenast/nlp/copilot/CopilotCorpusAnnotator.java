package com.condenast.nlp.copilot;

import com.condenast.nlp.Annotation;
import com.condenast.nlp.Annotations;
import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.condenast.nlp.Annotations.ALL_ANNOTATIONS_FILTER;
import static com.condenast.nlp.opennlp.ResourceUtil.copyBratConfigFilesIfNotExist;

/**
 * Created by arau on 10/20/15.
 */
public class CopilotCorpusAnnotator extends AbstractVisitor {

    private static final Map<String, String> analysisNameFileOrder;

    static {
        analysisNameFileOrder = new HashMap<>();
        analysisNameFileOrder.put("model.hed", "001");
        analysisNameFileOrder.put("model.caption", "001");
        analysisNameFileOrder.put("model.title", "001");

        analysisNameFileOrder.put("model.dek", "002");
        analysisNameFileOrder.put("model.description", "002");

        analysisNameFileOrder.put("model.body", "003");
    }

    private Predicate<? super Annotation> annotationFilter;

    private File outAnnotationDir;

    public CopilotCorpusAnnotator(final File outAnnotationDir) {
        this(outAnnotationDir, ALL_ANNOTATIONS_FILTER());
    }

    public CopilotCorpusAnnotator(final File outAnnotationDir, Predicate<? super Annotation> annotationFilter) {
        super(Integer.MAX_VALUE);
        Validate.notNull(outAnnotationDir);
        Validate.notNull(annotationFilter);
        this.outAnnotationDir = outAnnotationDir;
        this.annotationFilter = annotationFilter;
    }

    @Override
    protected void processDocument(CopilotDocument copilotDocument) {
        CopilotDocumentAnnotator documentAnnotator = CopilodDocumentAnnotatorFactory.buildFor(copilotDocument);
        documentAnnotator.annotate();
        writeAnnotations(documentAnnotator.getAnnotatedCopilotDocument());
    }

    @Override
    public void onError(Exception e) {
        if (currentDocument != null) {
            String cdInfo = String.format(">%s.%s.%s< | uri=%s", currentDocument.brandName(), currentDocument.collectionName(), currentDocument.id(), currentDocument.uri());
            log.error(e.getMessage() + " | CurrentDocument Error Context: " + cdInfo);
        }
        super.onError(e);
    }

    protected void writeAnnotations(AnnotatedCopilotDocument annotatedCopilotDocument) {
        annotatedCopilotDocument.analyses().forEach((analysisName, analysis) -> {
            String uuid = annotatedCopilotDocument.copilotDocument().id();
            File baseDirFile = baseDirFile(annotatedCopilotDocument.copilotDocument());
            copyBratConfigFilesIfNotExist(baseDirFile);
            File txtFile = new File(baseDirFile, txtFileName(annotatedCopilotDocument.copilotDocument(), analysisName, uuid));
            tryWriteToFile(analysis.text(), txtFile);
            File annFile = new File(baseDirFile, annFileName(annotatedCopilotDocument.copilotDocument(), analysisName, uuid));
            String bratAnnotations = Annotations.toBratFormat(analysis.annotations(), this.annotationFilter);
            tryWriteToFile(bratAnnotations, annFile);
        });
    }

    protected String annFileName(CopilotDocument copilotDocument, String analysisName, String uuid) {
        return baseFileName(copilotDocument, analysisName, uuid).concat(".ann");
    }

    protected String baseFileName(CopilotDocument copilotDocument, String analysisName, String uuid) {
        return String.format("%s.%s.%s-%s-%s", copilotDocument.brandName(), copilotDocument.collectionName(), uuid, fileNameOrder(analysisName), analysisName);
    }

    protected File baseDirFile(CopilotDocument copilotDocument) {
        return new File(outAnnotationDir, String.format("%s/%s", copilotDocument.brandName(), copilotDocument.collectionName()));
    }

    private String fileNameOrder(String analysisName) {
        String s = analysisNameFileOrder.get(analysisName);
        if (s == null) s = "NULL";
        return s;
    }

    protected String txtFileName(CopilotDocument copilotDocument, String analysisName, String uuid) {
        return baseFileName(copilotDocument, analysisName, uuid).concat(".txt");
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
