package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.AbstractAnalyzer;
import com.condenast.nlp.Annotation;
import opennlp.tools.parser.Parse;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.condenast.nlp.opennlp.ChunksExtractorAnalyzer.NP_ANNOTATION;
import static com.condenast.nlp.opennlp.ChunksExtractorAnalyzer.PARTS_FEATURE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChunksExtractorAnalyzerTest {

    @Before
    public void setUp() throws Exception {

    }

    String testPunctBug = "“Mark’s obsessive about what he lets into a house,” Levine says, “and I inherited his " + "sickness.” Haddawy was particularly prudent in selecting just the right carpet—a 1920s Persian—to accompany the midcentury furnishings in the living room.";

    @Test
    public void testPunctBug() throws Exception {
        AnalysisContext context = new AnalysisContext(testPunctBug);
        AbstractAnalyzer chunkingAnalyzer = new ChunksExtractorAnalyzer(context);
        chunkingAnalyzer.analyze();

        List<Annotation> annotations = context.annotations();
        assertNotNull(annotations);
        List<ShowableParse> parts = (List<ShowableParse>) annotations.get(annotations.size() - 2).getFeature(PARTS_FEATURE);
        String room = parts.get(1).getCoveredText();
        assertEquals("room", room);
    }


    @Test
    public void testAnalyzePP() throws Exception {
        String text = "The House of Deputies is full.";

        AnalysisContext context = new AnalysisContext(text);
        AbstractAnalyzer chunkingAnalyzer = new ChunksExtractorAnalyzer(context);
        chunkingAnalyzer.analyze();

        List<Annotation> ppAnnotations = context.annotations("PP_ANNOTATION");
        assertNotNull(ppAnnotations);
        assertEquals(1, ppAnnotations.size());

    }

    @Test
    public void testAnalyze() throws Exception {
        String text = "Former first lady Nancy Reagan was taken to a " +
                "suburban Los Angeles " +
                "hospital \"as a precaution\" Sunday after a " +
                "fall at her home, an " +
                "aide said. " +
                "The 86-year-old Reagan will remain overnight for " +
                "observation at a hospital in Santa Monica, California, " +
                "said Joanne " +
                "Drake, chief of staff for the Reagan Foundation. The Doctors are confident about his recovery.";

        AnalysisContext context = new AnalysisContext(text);
        ChunksExtractorAnalyzer chunkingAnalyzer = new ChunksExtractorAnalyzer(context);
        chunkingAnalyzer.analyze();

        assertNotNull(chunkingAnalyzer.myAnnotations());
        List<Annotation> npAnnotations = context.annotations(NP_ANNOTATION);
        System.out.println(npAnnotations);
        assertEquals(17, npAnnotations.size());

        Annotation npAnnotation = npAnnotations.get(0);
        assertEquals("Former first lady Nancy Reagan", npAnnotation.text());
        assertNotNull(npAnnotation.getFeature(PARTS_FEATURE));
        List<Parse> npPartsFeature = (List<Parse>) npAnnotation.getFeature(PARTS_FEATURE);
        assertEquals(5, npPartsFeature.size());

        npAnnotation = npAnnotations.get(15);
        assertEquals("Doctors", npAnnotation.text());
        assertNotNull(npAnnotation.getFeature(PARTS_FEATURE));
        npPartsFeature = (List<Parse>) npAnnotation.getFeature(PARTS_FEATURE);
        assertEquals(1, npPartsFeature.size());

        assertIsLemmatized(npAnnotation);


    }

    private void assertIsLemmatized(Annotation npAnnotation) {
        assertEquals("doctor", ((List<Parse>) npAnnotation.getFeature(PARTS_FEATURE)).get(0).getLabel());
    }
}