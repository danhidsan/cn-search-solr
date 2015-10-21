package com.condenast.nlp;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/20/15.
 */
public class AnalyzerPipeline extends Analyzer {

    private List<Analyzer> pipeLine;

    private AnalyzerPipeline(AnalysisContext context) {
        super(context);
    }

    public static AnalyzerPipeline assemble(String text, List<Class<? extends Analyzer>> analyzerClasses) {
        Validate.notEmpty(analyzerClasses);
        Validate.notEmpty(text);
        AnalysisContext analysisContext = new AnalysisContext(text);
        AnalyzerPipeline instance = new AnalyzerPipeline(analysisContext);
        instance.pipeLine = analyzerClasses.stream().map(instantiateAnalyzerFunc(analysisContext)).collect(toList());
        return instance;
    }

    private static Function<Class<? extends Analyzer>, ? extends Analyzer> instantiateAnalyzerFunc(AnalysisContext analysisContext) {
        return ac -> tryInstantiateAnalyzer(analysisContext, ac);
    }

    private static Analyzer tryInstantiateAnalyzer(AnalysisContext analysisContext, Class<? extends Analyzer> ac) {
        try {
            return ac.getConstructor(AnalysisContext.class).newInstance(analysisContext);
        } catch (Exception e) {
            throw new NLPException(e);
        }
    }

    @Override
    public List<String> myTypes() {
        List<String> allPossibleTypes = new ArrayList<>();
        pipeLine.stream().forEach(a -> allPossibleTypes.addAll(a.myTypes()));
        return allPossibleTypes;
    }

    @Override
    public void analyze() {
        pipeLine.forEach(Analyzer::analyze);
    }

}
