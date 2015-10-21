package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.Annotation;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_TYPE;
import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Created by arau on 10/15/15.
 */
public class NERAnalyzer extends Analyzer {

    private final NameFinderME[] finders;
    private final List<String> defaultNerTypes = unmodifiableList(Arrays.asList("person", "location", "organization", "money", "time", "date", "percentage"));
    private List<String> myTypes;

    public NERAnalyzer(AnalysisContext context) {
        this(context, null);
    }

    public NERAnalyzer(AnalysisContext context, List<String> nerTypes) {
        super(context);
        myTypes = isEmpty(nerTypes) ? defaultNerTypes : nerTypes;
        finders = new NameFinderME[myTypes.size()];
        for (int mi = 0; mi < myTypes.size(); mi++) {
            String modelFileName = modelName(myTypes.get(mi));
            finders[mi] = new NameFinderME(ResourceUtil.modelOf(modelFileName, PooledTokenNameFinderModel.class));
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
            offset = context.annotations(SENTENCE_TYPE).get(si).getSpan().getEnd() + 1;
        }
        removeConflicts(allAnnotations);
        analysis().addAnnotations(allAnnotations);
    }


    @Override
    public List<String> myTypes() {
        return myTypes;
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
}
