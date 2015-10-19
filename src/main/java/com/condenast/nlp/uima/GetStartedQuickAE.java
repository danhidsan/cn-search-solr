package com.condenast.nlp.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

/**
 * Created by arau on 10/16/15.
 */
public class GetStartedQuickAE extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {

    public static final String PARAM_STRING = "stringParam";
    @ConfigurationParameter(name = PARAM_STRING)
    private String stringParam;

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        System.out.println("Hello world!  Say 'hi' to " + stringParam);

    }
}