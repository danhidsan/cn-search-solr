package com.condenast.search.solr.copilot.indexer;

import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.copilot.mapper.CnOneMapping;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CopilotImporterTest {

    private Importer copilotImporter;
    private CorporaWalkerFS corporaWalkerFS_Test;
    private SimpleCopilotImporterListener testListener;

    @Before
    public void setup() {
        URL dir = this.getClass().getResource("/");
        File rootDir = new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod");
        assertTrue(rootDir.exists());
        corporaWalkerFS_Test = new CorporaWalkerFS(rootDir);
        testListener = new SimpleCopilotImporterListener();
        copilotImporter = Importer.withCorporaWalker(corporaWalkerFS_Test).
                andDocMappers(CnOneMapping.build()).
                andListeners(testListener).andMaxDocs(1).build();
    }

    @Test
    public void testRun() throws Exception {
        copilotImporter.run();
        assertEquals(1, testListener.solrInputDocuments.size());
        SolrInputDocument solrInputDocument = testListener.solrInputDocuments.get(0);
        assertEquals(23, solrInputDocument.size());
    }

}