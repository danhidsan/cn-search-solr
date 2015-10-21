package com.condenast.nlp.copilot;

import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;

public class CopilodDocumentAnnotatorFactoryTest {

    private File anArticleFile;
    private CopilotDocumentFile copilotDocument;

    @Before
    public void setup() throws IOException {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus//oneDocOneBrand/prod/cnt/articles/anArticle.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
    }

    @Test
    public void testBuildForArticles() throws Exception {
        CopilotDocumentAnnotator copilotDocumentAnnotator = CopilodDocumentAnnotatorFactory.buildFor(copilotDocument);
        assertNotNull(copilotDocumentAnnotator);
    }

}