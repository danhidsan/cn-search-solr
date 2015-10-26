package com.condenast.nlp;


import opennlp.tools.util.Span;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arau on 10/15/15.
 */
public class Annotation implements Comparable<Annotation> {

    public static final String ANNOTATOR_NOTES = "AnnotatorNotes";
    public static final String BRAT_NOTES_TEMLPATE = "%s\t%s %s\t%s: %s\n";
    public static final String BRAT_TEXT_TEMPLATE = "%s\t%s %s %s\t%s";
    private Span span;
    private String type;
    private double prob;
    private final AnalysisContext context;
    private final Map<String, Object> features = new HashMap<>();

    public Annotation(AnalysisContext context, String type, Span span, double prob) {
        Validate.notNull(context);
        Validate.notNull(span);
        Validate.notEmpty(type);
        this.span = span;
        this.type = type;
        this.prob = prob;
        this.context = context;
    }

    public Span getSpan() {
        return span;
    }

    public String getType() {
        return type;
    }

    public double getProb() {
        return prob;
    }

    public int compareTo(Annotation a) {
        int c = span.compareTo(a.span);
        if (c == 0) {
            c = Double.compare(prob, a.prob);
            if (c == 0) {
                c = type.compareTo(a.type);
            }
        }
        return c;
    }

    public void putFeature(String name, Object feature) {
        Validate.notEmpty(name);
        Validate.notNull(feature);
        features.put(name, feature);
    }

    public Object getFeature(String name) {
        Validate.notEmpty(name);
        return features.get(name);
    }

    public Map<String, Object> features() {
        return features;
    }

    public String text() {
        return span.getCoveredText(context.text()).toString();
    }

    public String toString() {
        return "type=" + type + " span=" + span + " prob=" + prob + " text=" + text();
    }

    public String toBratFormat(AtomicInteger counter) {
        StringBuilder stringBuilder = new StringBuilder();
        String textNotesId = toBratTextAnnotation(counter, stringBuilder);
        toBratNotesFeatures(counter, stringBuilder, textNotesId);
        toBratNotesAnnotationString(counter, stringBuilder, textNotesId);
        return stringBuilder.toString();
    }

    private String toBratTextAnnotation(AtomicInteger counter, StringBuilder stringBuilder) {
        String textNotesId = "T" + counter.getAndIncrement();
        String brat = String.format(BRAT_TEXT_TEMPLATE, textNotesId, getType(), getSpan().getStart(), getSpan().getEnd(), text());
        stringBuilder.append(brat);
        return textNotesId;
    }

    private void toBratNotesAnnotationString(AtomicInteger counter, StringBuilder stringBuilder, String textNotesId) {
        String annotationNote = String.format(BRAT_NOTES_TEMLPATE, annNotesId(counter), ANNOTATOR_NOTES, textNotesId, "ANNOTATION", this.toString());
        stringBuilder.append("\n").append(annotationNote);
    }

    private void toBratNotesFeatures(AtomicInteger counter, StringBuilder stringBuilder, String textNotesId) {
        if (features().isEmpty()) return;
        stringBuilder.append("\n");
        features().forEach((k, v) -> {
            String brat = String.format(BRAT_NOTES_TEMLPATE, annNotesId(counter), ANNOTATOR_NOTES, textNotesId, k, v);
            stringBuilder.append(brat);
        });
    }

    private String annNotesId(AtomicInteger counter) {
        return "#" + counter.getAndIncrement();
    }

}
