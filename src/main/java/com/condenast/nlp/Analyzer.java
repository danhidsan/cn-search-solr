package com.condenast.nlp;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;

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

    public AnalysisContext context() {
        return context;
    }

    public List<Annotation> myAnnotations() {
        List<Annotation> myAnnotations = new ArrayList<>();
        myTypes().forEach(t -> myAnnotations.addAll(context.annotations(t)));
        return unmodifiableList(myAnnotations);
    }

    public abstract List<String> myTypes();

    public abstract void analyze();

}
