package com.condenast.nlp;

import com.condenast.nlp.opennlp.SentenceDetectorAnalyzer;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_ANNOTATION;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/16/15.
 */
public abstract class AbstractAnalyzer implements Analyzer {

    protected final AnalysisContext context;

    public AbstractAnalyzer(AnalysisContext context) {
        Validate.notNull(context);
        this.context = context;
    }

    @Override
    public AnalysisContext analysis() {
        return context;
    }

    @Override
    public List<Annotation> myAnnotations() {
        List<Annotation> myAnnotations = new ArrayList<>();
        myTypes().forEach(t -> myAnnotations.addAll(context.annotations(t)));
        return unmodifiableList(myAnnotations);
    }

    protected List<String> detectSentences() {
        List<String> sentences = analysis().annotationTextFor(SENTENCE_ANNOTATION);
        if (sentences.isEmpty()) {
            SentenceDetectorAnalyzer sentenceDetectorAnalyzer = new SentenceDetectorAnalyzer(context);
            sentenceDetectorAnalyzer.analyze();
            sentences = analysis().annotationTextFor(SENTENCE_ANNOTATION);
        }
        return sentences;
    }


}
