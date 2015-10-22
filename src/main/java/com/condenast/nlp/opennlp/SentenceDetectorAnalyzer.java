package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/15/15.
 */
public class SentenceDetectorAnalyzer extends Analyzer {

    public static final String SENTENCE_ANNOTATION = "SENTENCE_ANNOTATION";
    private static final String MODEL_NAME = "en-sent.bin";
    private final SentenceDetectorME sentenceDetectorME;
    private final static List<String> myTypes = unmodifiableList(Arrays.asList(SENTENCE_ANNOTATION));

    public SentenceDetectorAnalyzer(AnalysisContext analysisContext) {
        super(analysisContext);
        sentenceDetectorME = new SentenceDetectorME(ResourceUtil.modelOf(MODEL_NAME, SentenceModel.class));
    }

    @Override
    public void analyze() {
        Span[] spans = sentenceDetectorME.sentPosDetect(context.text());
        AtomicInteger idx = new AtomicInteger(0);
        asList(spans).forEach(span -> context.addAnnotation(SENTENCE_ANNOTATION, span, prob(idx)));
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

    private double prob(AtomicInteger idx) {
        return sentenceDetectorME.getSentenceProbabilities()[idx.getAndIncrement()];
    }

}
