package com.condenast.nlp;

import java.util.List;

/**
 * Created by arau on 10/28/15.
 */
public interface Analyzer {
    AnalysisContext analysis();

    List<Annotation> myAnnotations();

    List<String> myTypes();

    void analyze();
}
