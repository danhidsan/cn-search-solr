package com.condenast.nlp.copilot;

import com.condenast.nlp.copilot.annotators.DefaultCopilotDocumentAnnotator;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.condenast.search.solr.SolrConfigTestHelper.testCopilotCorpus10DocsPerBrandPerCollectionRootDir;

public class CopilotCorpusAnnotatorTest {

    private File anArticleFile;
    private CopilotDocumentFile copilotDocument;
    private AnnotatedCopilotDocument annotatedCopilotDocument;
    private File testDir;

    @Before
    public void setup() throws IOException {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus/tenDocPerBrandPerCollection/prod/ad/articles/55e78aae302ba71f3017be76.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
        DefaultCopilotDocumentAnnotator defaultCopilotDocumentAnnotator = new DefaultCopilotDocumentAnnotator(copilotDocument);
        defaultCopilotDocumentAnnotator.annotate();
        annotatedCopilotDocument = defaultCopilotDocumentAnnotator.getAnnotatedCopilotDocument();
        testDir = new File(FileUtils.getTempDirectory(), "copilotAnnotationsTest");
        FileUtils.deleteDirectory(testDir);
        FileUtils.forceMkdir(testDir);
    }

    @Test
    public void testWriteAnnotations() throws Exception {
        CopilotCorpusAnnotator copilotCorpusAnnotator = new CopilotCorpusAnnotator(testDir);
        copilotCorpusAnnotator.writeAnnotations(annotatedCopilotDocument);

        File expectedTxtFile = new File(testDir, copilotCorpusAnnotator.txtFileName("model.hed", "55e78aae302ba71f3017be76"));
        Assert.assertTrue("txt file '" + expectedTxtFile + "' has not been created", expectedTxtFile.exists());
        String hedTxt = FileUtils.readFileToString(expectedTxtFile);
        Assert.assertEquals("Adam Levine’s Hollywood Hills Home", hedTxt);

        File expectedAnnFile = new File(testDir, copilotCorpusAnnotator.annFileName("model.hed", "55e78aae302ba71f3017be76"));
        Assert.assertTrue("ann file '" + expectedAnnFile + "' has not been created", expectedAnnFile.exists());
        String hedAnn = FileUtils.readFileToString(expectedAnnFile);
        System.out.println(hedAnn);
        String expectedAnn = "T1\tSENTENCE_ANNOTATION 0 34\tAdam Levine’s Hollywood Hills Home\n" +
                "T2\tperson 0 11\tAdam Levine\n" +
                "T3\tlocation 14 29\tHollywood Hills\n" +
                "T4\tNP_ANNOTATION 0 34\tAdam Levine’s Hollywood Hills Home\n" +
                "#5\tAnnotatorNotes T4\tLEMMATIZED_NGRAMS: [Adam Levine’s, Adam Levine’s Hollywood, Adam Levine’s Hollywood Hills, Adam Levine’s Hollywood Hills Home, Levine’s Hollywood, Levine’s Hollywood Hills, Levine’s Hollywood Hills Home, Hollywood Hills, Hollywood Hills Home, Hills Home]\n" +
                "#6\tAnnotatorNotes T4\tPARTS: [(NNP Adam), (NNP Levine’s), (NNP Hollywood), (NNP Hills), (NNP Home)]\n" +
                "\n";
        Assert.assertEquals(expectedAnn, hedAnn);
    }

    @Ignore
    @Test
    public void testAnnotateCorpus() throws Exception {
        CopilotCorpusAnnotator copilotCorpusAnnotator = new CopilotCorpusAnnotator(new File("./bratAnnotationsTest"));
        CorporaWalkerFS corporaWalker = new CorporaWalkerFS(testCopilotCorpus10DocsPerBrandPerCollectionRootDir());
        corporaWalker.run(copilotCorpusAnnotator);
    }

}