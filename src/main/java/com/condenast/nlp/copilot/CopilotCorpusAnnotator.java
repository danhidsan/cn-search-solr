package com.condenast.nlp.copilot;

import com.condenast.nlp.Annotation;
import com.condenast.nlp.Annotations;
import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;
import com.condenast.nlp.opennlp.ResourceUtil;
import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_ANNOTATION;

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

    @Override
    public void onError(Exception e) {
        super.onError(e);
        if (currentDocument == null) return;
        String cdInfo = String.format(">%s.%s.%s< | uri=%s", currentDocument.brandName(), currentDocument.collectionName(), currentDocument.id(), currentDocument.uri());
        log.error(e.getMessage() + " | CurrentDocument Error Context: " + cdInfo);
    }

    protected void writeAnnotations(AnnotatedCopilotDocument annotatedCopilotDocument) {
        annotatedCopilotDocument.analyses().forEach((analysisName, analysis) -> {
            String uuid = annotatedCopilotDocument.copilotDocument().id();
            File baseDirFile = baseDirFile(annotatedCopilotDocument.copilotDocument());
            copyVisualConfFileIfNotExists(baseDirFile);
            File txtFile = new File(baseDirFile, txtFileName(annotatedCopilotDocument.copilotDocument(), analysisName, uuid));
            tryWriteToFile(analysis.text(), txtFile);
            File annFile = new File(baseDirFile, annFileName(annotatedCopilotDocument.copilotDocument(), analysisName, uuid));
            String bratAnnotations = Annotations.toBratFormat(analysis.annotations(), filterByPassedAnn());
            tryWriteToFile(bratAnnotations, annFile);
        });
    }

    private Predicate<? super Annotation> filterByPassedAnn() {
        return a -> !(a.getType().equals(SENTENCE_ANNOTATION) || a.getType().equals(ChunksExtractorAnalyzer.VP_ANNOTATION));
    }

    private void copyVisualConfFileIfNotExists(File baseDirFile) {
        File destVisualConf = new File(baseDirFile, ResourceUtil.VISUAL_CONF_FILENAME);
        if (!destVisualConf.exists()) {
            try {
                FileUtils.copyFile(ResourceUtil.visualConfTemplateFile(), destVisualConf);
            } catch (IOException e) {
                log.warn("Cannot copy visual.conf to: " + destVisualConf.getAbsolutePath());
            }
        }
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
