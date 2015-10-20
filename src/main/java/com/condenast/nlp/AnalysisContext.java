package com.condenast.nlp;

import opennlp.tools.util.Span;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/16/15.
 */
public class AnalysisContext {

    private final String text;
    private List<Annotation> annotations = new ArrayList<>();

    public AnalysisContext(final String text) {
        Validate.notEmpty(text);
        this.text = text;
    }

    public String text() {
        return text;
    }

    public List<Annotation> annotations() {
        return unmodifiableList(annotations);
    }

    public Annotation addAnnotation(String type, Span span, double prob) {
        Annotation annotation = new Annotation(this, type, span, prob);
        annotations.add(annotation);
        return annotation;
    }

    public List<Annotation> annotations(String type) {
        return annotations.stream().filter(isOfType(type)).collect(toList());
    }

    public static Predicate<Annotation> isOfType(String type) {
        return (a -> a.getType().equals(type));
    }

    public List<String> annotationTextFor(String type) {
        return annotations(type).stream().map(Annotation::text).collect(Collectors.toList());
    }

    public void addAnnotations(List<Annotation> allAnnotations) {
        Validate.notNull(allAnnotations);
        annotations.addAll(allAnnotations);
    }
}
