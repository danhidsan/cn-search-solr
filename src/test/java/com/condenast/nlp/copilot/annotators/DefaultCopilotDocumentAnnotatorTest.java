package com.condenast.nlp.copilot.annotators;

import com.condenast.nlp.copilot.AnnotatedCopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DefaultCopilotDocumentAnnotatorTest {

    private static DefaultCopilotDocumentAnnotator defaultCopilotDocumentAnnotator;
    private static AnnotatedCopilotDocument annotatedCopilotDocument;

    @BeforeClass
    public static void setUp() throws Exception {
        URL dir = DefaultCopilotDocumentAnnotatorTest.class.getResource("/");
        File anArticleFile = new File(dir.getPath() + "/copilotCorpus/tenDocPerBrandPerCollection/prod/ad/articles/55e78aae302ba71f3017be76.json");
        CopilotDocumentFile copilotDocument = new CopilotDocumentFile(anArticleFile);
        defaultCopilotDocumentAnnotator = new DefaultCopilotDocumentAnnotator();
        defaultCopilotDocumentAnnotator.setCopilotDocument(copilotDocument);
        defaultCopilotDocumentAnnotator.annotate();
        annotatedCopilotDocument = defaultCopilotDocumentAnnotator.getAnnotatedCopilotDocument();
    }

    @Test
    public void testAnnotateArticle() throws Exception {
        System.out.println(annotatedCopilotDocument.analyses());
        assertNotNull(annotatedCopilotDocument);
        assertNotNull(annotatedCopilotDocument.analyses());
        assertEquals(3, annotatedCopilotDocument.analyses().size());
        DefaultCopilotDocumentAnnotator.annotableModelFields.stream().forEach(field -> assertNotNull(annotatedCopilotDocument.analysisOf("model." + field)));
    }

    @Test
    public void testTextCleaned() {
        String text = annotatedCopilotDocument.analysisOf("model.body").text();
        assertNotNull(text);
        assertFalse("Text has not been cleaned from markup", text.contains("[View Slideshow](http://www" +
                ".architecturaldigest" +
                ".com/gallery/adam-levine-hollywood-hills-home-slideshow)"));
        assertFalse("Text has not been cleaned from copilot specific stuff", text.startsWith("View Slideshow"));
    }

}