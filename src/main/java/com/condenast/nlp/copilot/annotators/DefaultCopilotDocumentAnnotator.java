package com.condenast.nlp.copilot.annotators;

import com.condenast.nlp.AnalyzerPipeline;
import com.condenast.nlp.TextHelper;
import com.condenast.nlp.copilot.AnnotatedCopilotDocument;
import com.condenast.nlp.copilot.CopilotDocumentAnnotator;
import com.condenast.nlp.opennlp.DefaultOpenNlpPipeline;
import com.condenast.search.corpus.utils.JsonObj;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/20/15.
 */
public class DefaultCopilotDocumentAnnotator implements CopilotDocumentAnnotator {

    public static final List<String> annotableModelFields = unmodifiableList(asList("body", "dek", "hed"));
    private JsonObj model;

    protected CopilotDocument copilotDocument;
    protected AnnotatedCopilotDocumentImpl annotatedCopilotDocument;

    public DefaultCopilotDocumentAnnotator() {
    }

    public DefaultCopilotDocumentAnnotator(final CopilotDocument copilotDocument) {
        this.setCopilotDocument(copilotDocument);
    }

    @Override
    public void setCopilotDocument(final CopilotDocument copilotDocument) {
        Validate.notNull(copilotDocument);
        this.copilotDocument = copilotDocument;
        this.annotatedCopilotDocument = new AnnotatedCopilotDocumentImpl(copilotDocument);
        this.model = JsonObj.fromJson(copilotDocument.toJson()).obj("model");
        Validate.notNull(this.model, "CopilotDocument model is null for copilotDocument.toJson: " + copilotDocument.toJson());
    }

    @Override
    public AnnotatedCopilotDocument getAnnotatedCopilotDocument() {
        return annotatedCopilotDocument;
    }

    @Override
    public void annotate() {
        annotableModelFields.stream().filter(fieldContentIsNotBlank()).forEach(this::annotateModel);
    }

    private Predicate<String> fieldContentIsNotBlank() {
        return annotableModelField -> StringUtils.isNotBlank(model.string(annotableModelField));
    }

    private void annotateModel(String annotableField) {
        String annotableFieldContent = model.string(annotableField);
        String cleanedAnnotableFieldContent = TextHelper.cleanCopilotField(annotableFieldContent);
        if (StringUtils.isBlank(cleanedAnnotableFieldContent)) return;
        AnalyzerPipeline pipeline = DefaultOpenNlpPipeline.withText(cleanedAnnotableFieldContent);
        pipeline.analyze();
        annotatedCopilotDocument.addAnalysis("model." + annotableField, pipeline.analysis());
    }

}
