package com.condenast.nlp.copilot;

import com.condenast.nlp.copilot.annotators.DefaultCopilotDocumentAnnotator;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.condenast.search.solr.SolrConfigTestHelper.testCopilotCorpus10DocsPerBrandPerCollectionRootDir;
import static org.junit.Assert.assertEquals;

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
        File baseDir = copilotCorpusAnnotator.baseDirFile(annotatedCopilotDocument.copilotDocument());
        File expectedTxtFile = new File(baseDir, copilotCorpusAnnotator.txtFileName(annotatedCopilotDocument.copilotDocument(), "model" + ".hed", "55e78aae302ba71f3017be76"));
        Assert.assertTrue("txt file '" + expectedTxtFile + "' has not been created", expectedTxtFile.exists());
        String hedTxt = FileUtils.readFileToString(expectedTxtFile);
        assertEquals("Adam Levine’s Hollywood Hills Home", hedTxt);

        File expectedAnnFile = new File(baseDir, copilotCorpusAnnotator.annFileName(annotatedCopilotDocument.copilotDocument(), "model.hed", "55e78aae302ba71f3017be76"));
        Assert.assertTrue("ann file '" + expectedAnnFile + "' has not been created", expectedAnnFile.exists());
        String hedAnn = FileUtils.readFileToString(expectedAnnFile);
        System.out.println(hedAnn);
        String expectedAnn = "T1\tSENTENCE_ANNOTATION 0 34\tAdam Levine’s Hollywood Hills Home\n" +
                "#2\tAnnotatorNotes T1\tANNOTATION: type=SENTENCE_ANNOTATION span=[0..34) prob=1.0 text=Adam Levine’s Hollywood Hills Home\n" +
                "T3\tperson 0 11\tAdam Levine\n" +
                "#4\tAnnotatorNotes T3\tANNOTATION: type=person span=[0..11) prob=0.9457607245957048 text=Adam Levine\n" +
                "T5\tlocation 14 29\tHollywood Hills\n" +
                "#6\tAnnotatorNotes T5\tANNOTATION: type=location span=[14..29) prob=0.9024026176240991 text=Hollywood Hills\n" +
                "T7\tNP_ANNOTATION 0 34\tAdam Levine’s Hollywood Hills Home\n" +
                "#8\tAnnotatorNotes T7\tLEMMATIZED_NGRAMS: [Adam, Adam Levine’s, Adam Levine’s Hollywood, Adam Levine’s Hollywood Hills, Adam Levine’s Hollywood Hills Home, Levine’s, Levine’s Hollywood, Levine’s Hollywood Hills, Levine’s Hollywood Hills Home, Hollywood, Hollywood Hills, Hollywood Hills Home, Hills, Hills Home, Home]\n" +
                "#9\tAnnotatorNotes T7\tPARTS: [(NNP Adam), (NNP Levine’s), (NNP Hollywood), (NNP Hills), (NNP Home)]\n" +
                "#10\tAnnotatorNotes T7\tANNOTATION: type=NP_ANNOTATION span=[0..34) prob=0.8202670966236225 " +
                "text=Adam Levine’s Hollywood Hills Home\n";
        assertEquals(expectedAnn, hedAnn);
    }

    @Test
    public void testAnnotateCorpus() throws Exception {
        File outAnnotationDir = new File("./_bratAnnotationsTest_");
        FileUtils.deleteDirectory(outAnnotationDir);
        FileUtils.forceMkdir(outAnnotationDir);
        CopilotCorpusAnnotator annotator = new CopilotCorpusAnnotator(outAnnotationDir, CopilotCorpusAnnotator.ONLY_NER_AND_NP());
        CorporaWalkerFS corporaWalker = new CorporaWalkerFS(testCopilotCorpus10DocsPerBrandPerCollectionRootDir());
        corporaWalker.run(annotator);
        assertEquals(0, annotator.counterErr());
    }

}