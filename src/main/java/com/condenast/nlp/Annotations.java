package com.condenast.nlp;

import com.condenast.nlp.opennlp.ChunksExtractorAnalyzer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * Created by arau on 10/21/15.
 */
public class Annotations {

    public static String toBratFormat(List<Annotation> annotations) {
        return toBratFormat(annotations, (a -> true));
    }

    public static String toBratFormat(List<Annotation> annotations, Predicate<? super Annotation> filter) {
        StringBuilder builder = new StringBuilder();
        AtomicInteger id = new AtomicInteger(1);
        annotations.stream().filter(filter).forEach(a -> builder.append(a.toBratFormat(id)).append("\n"));
        return builder.toString().replaceAll("\n\n", "\n");
    }

    public static Predicate<? super Annotation> ALL_ANNOTATIONS_PREDICATE() {
        return a -> (true);
    }

    public static Predicate<? super Annotation> ONLY_NER_AND_NP_PREDICATE() {
        return a -> (a.getType().equals(ChunksExtractorAnalyzer.NP_ANNOTATION) ||
                a.getType().equals("person") ||
                a.getType().equals("location") ||
                a.getType().equals("organization"));
    }

}
