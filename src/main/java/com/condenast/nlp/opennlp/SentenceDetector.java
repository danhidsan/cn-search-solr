package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.NLPException;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/15/15.
 */
public class SentenceDetector extends Analyzer {

    public static final String SENTENCE_TYPE = "SENTENCE";
    private static final String MODEL_NAME = "en-sent.bin";
    private final SentenceDetectorME sentenceDetectorME;
    private static SentenceModel model;
    private final static List<String> myTypes = unmodifiableList(Arrays.asList(SENTENCE_TYPE));

    public SentenceDetector(AnalysisContext analysisContext) {
        super(analysisContext);
        try {
            initModelIfNeeded();
            sentenceDetectorME = new SentenceDetectorME(model);
        } catch (IOException e) {
            throw new NLPException(e);
        }
    }

    private static void initModelIfNeeded() throws IOException {
        if (model == null) {
            InputStream modelInputStream = null;
            try {
                modelInputStream = new FileInputStream(ModelUtil.fileOf(MODEL_NAME));
                model = new SentenceModel(modelInputStream);
            } finally {
                close(modelInputStream);
            }
        }
    }

    private static void close(InputStream modelIn) {
        if (modelIn != null) {
            try {
                modelIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void analyze() {
        Span[] spans = sentenceDetectorME.sentPosDetect(context.text());
        AtomicInteger idx = new AtomicInteger(0);
        asList(spans).forEach(span -> context.addAnnotation(SENTENCE_TYPE, span, prob(idx)));
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

    private double prob(AtomicInteger idx) {
        return sentenceDetectorME.getSentenceProbabilities()[idx.getAndIncrement()];
    }

}
