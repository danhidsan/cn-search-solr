package com.condenast.nlp.uima;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

public class GetStartedQuickAETest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testProcess() throws Exception {
        JCas jCas = JCasFactory.createJCas();

        AnalysisEngine analysisEngine = AnalysisEngineFactory.createEngine(GetStartedQuickAE.class, GetStartedQuickAE.PARAM_STRING, "uimaFIT");

        analysisEngine.process(jCas);

        jCas.getAnnotationIndex().forEach(a -> {
            System.out.println(">>>>>" + a.toString());
        });

        System.out.println(jCas);

        System.out.println(jCas.size());

    }
}