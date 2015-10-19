package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.Annotation;
import com.condenast.nlp.NLPException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.condenast.nlp.opennlp.SentenceDetector.SENTENCE_TYPE;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/15/15.
 */
public class NER extends Analyzer {

    public static final Map<String, PooledTokenNameFinderModel> modelCacheByName = new HashMap<>();
    private final NameFinderME[] finders;
    private final List<String> myTypes = unmodifiableList(Arrays.asList("person", "location", "date"));

    public NER(AnalysisContext context) {
        super(context);
        finders = new NameFinderME[myTypes.size()];
        for (int mi = 0; mi < myTypes.size(); mi++) {
            finders[mi] = new NameFinderME(modelOf(myTypes.get(mi)));
        }
    }

    private PooledTokenNameFinderModel modelOf(String name) {
        if (!modelCacheByName.containsKey(name)) {
            InputStream modelIn = null;
            try {
                modelIn = new FileInputStream(ModelUtil.fileOf(modelName(name)));
                PooledTokenNameFinderModel model = new PooledTokenNameFinderModel(modelIn);
                modelCacheByName.put(name, model);
            } catch (Exception e) {
                throw new NLPException(e);
            } finally {
                if (modelIn != null) {
                    try {
                        modelIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return modelCacheByName.get(name);
    }

    private String modelName(String name) {
        return "en-ner-" + name + ".bin";
    }

    private void removeConflicts(List<Annotation> allAnnotations) {
        if (allAnnotations.size() < 2) return;
        sort(allAnnotations);
        List<Annotation> stack = new ArrayList<>();
        stack.add(allAnnotations.get(0));
        for (int ai = 1; ai < allAnnotations.size(); ai++) {
            Annotation curr = allAnnotations.get(ai);
            boolean deleteCurr = false;
            for (int ki = stack.size() - 1; ki >= 0; ki--) {
                Annotation prev = stack.get(ki);
                if (prev.getSpan().equals(curr.getSpan())) {
                    if (prev.getProb() > curr.getProb()) {
                        deleteCurr = true;
                        break;
                    } else {
                        allAnnotations.remove(stack.remove(ki));
                        ai--;
                    }
                } else if (prev.getSpan().intersects(curr.getSpan())) {
                    if (prev.getProb() > curr.getProb()) {
                        deleteCurr = true;
                        break;
                    } else {
                        allAnnotations.remove(stack.remove(ki));
                        ai--;
                    }
                } else if (prev.getSpan().contains(curr.getSpan())) {
                    break;
                } else {
                    stack.remove(ki);
                }
            }
            if (deleteCurr) {
                allAnnotations.remove(ai);
                ai--;
            } else {
                stack.add(curr);
            }
        }
    }


    @Override
    public void analyze() {
        Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
        List<String> sentences = detectSentences();
        List<Annotation> allAnnotations = new ArrayList<>();
        int offset = 0;
        for (int si = 0; si < sentences.size(); si++) {
            String sentence = sentences.get(si);
            Span[] tokenSpans = tokenizer.tokenizePos(sentence);
            String[] tokens = Span.spansToStrings(tokenSpans, sentence);
            for (int fi = 0; fi < finders.length; fi++) {
                Span[] names = finders[fi].find(tokens);
                double[] probs = finders[fi].probs(names);
                for (int ni = 0; ni < names.length; ni++) {
                    Span startSpan = tokenSpans[names[ni].getStart()];
                    int nameStart = offset + startSpan.getStart();
                    Span endSpan = tokenSpans[names[ni].getEnd() - 1];
                    int nameEnd = offset + endSpan.getEnd();
                    Span span = new Span(nameStart, nameEnd);
                    allAnnotations.add(new Annotation(context, myTypes.get(fi), span, probs[ni]));
                }
            }
            offset += context.annotations(SENTENCE_TYPE).get(si).getSpan().getEnd() + 1;
        }
        removeConflicts(allAnnotations);
        context().addAnnotations(allAnnotations);
    }

    private List<String> detectSentences() {
        List<String> sentences = context().annotationTextFor(SENTENCE_TYPE);
        if (sentences.isEmpty()) {
            SentenceDetector sentenceDetector = new SentenceDetector(context);
            sentenceDetector.analyze();
            sentences = context().annotationTextFor(SENTENCE_TYPE);
        }
        return sentences;
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }
}
