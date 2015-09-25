package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.AbstractIT;
import org.apache.solr.common.SolrInputDocument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.condenast.search.solr.SolrConfigTestHelper.testCopilotCorpus10DocsPerBrandPerCollectionRootDir;

/**
 * Created by arau on 9/8/15.
 */
public class CopilotImporterIT extends AbstractIT {

    private Importer copilotImporter;
    private CorporaWalkerFS corporaWalkerFS_Test;
    private TestSolrImporter testSolrListener;
    private File rootDir;
    private int maxDocsToImportPerCollection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCnOneEditIdx();
        jsonResponse();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        assertU(delQ("id:*"));
    }

    private void runImporter() {
        corporaWalkerFS_Test = new CorporaWalkerFS(rootDir);
        testSolrListener = new TestSolrImporter();
        copilotImporter = Importer.withCorporaWalker(corporaWalkerFS_Test).andListeners(testSolrListener).andMaxDocs(maxDocsToImportPerCollection).build();
        copilotImporter.run();
    }

    @Test
    public void testImport1DocsPerBrandPerCollection() throws Exception {
        maxDocsToImportPerCollection = 1;
        rootDir = testCopilotCorpus10DocsPerBrandPerCollectionRootDir();
        runImporter();
        assertTrue(testSolrListener.solrInputDocumentList.size() > 0);
        String id = testSolrListener.solrInputDocumentList.get(0).get("id").getValue().toString();
        assertQJNumFound("id:" + id, 1);
        assertQJNumFound("id:*", 77);
    }

    public static class TestSolrImporter implements ImporterListener {

        private List<SolrInputDocument> solrInputDocumentList = new ArrayList<>();

        @Override
        public void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
            solrInputDocumentList.add(solrInputDocument);
            assertU(adoc(solrInputDocument));
        }

        @Override
        public void onError(Exception e, CopilotDocument currentCopilotDocument, SolrInputDocument currentSolrInputDocument) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        @Override
        public void onStart(Importer importer) {

        }

        @Override
        public void onEnd(Importer importer) {
            assertU(commit());
            assertU(optimize());
        }

    }

}
