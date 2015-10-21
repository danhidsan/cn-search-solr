package com.condenast.nlp;

import com.condenast.nlp.opennlp.SentenceDetectorAnalyzer;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_TYPE;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/16/15.
 */
public abstract class Analyzer {

    protected final AnalysisContext context;

    public Analyzer(AnalysisContext context) {
        Validate.notNull(context);
        this.context = context;
    }

    public AnalysisContext analysis() {
        return context;
    }

    public List<Annotation> myAnnotations() {
        List<Annotation> myAnnotations = new ArrayList<>();
        myTypes().forEach(t -> myAnnotations.addAll(context.annotations(t)));
        return unmodifiableList(myAnnotations);
    }

    public abstract List<String> myTypes();

    public abstract void analyze();

    protected List<String> detectSentences() {
        List<String> sentences = analysis().annotationTextFor(SENTENCE_TYPE);
        if (sentences.isEmpty()) {
            SentenceDetectorAnalyzer sentenceDetectorAnalyzer = new SentenceDetectorAnalyzer(context);
            sentenceDetectorAnalyzer.analyze();
            sentences = analysis().annotationTextFor(SENTENCE_TYPE);
        }
        return sentences;
    }


}
