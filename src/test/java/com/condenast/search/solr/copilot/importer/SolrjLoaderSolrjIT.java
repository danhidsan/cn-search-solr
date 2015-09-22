package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.AbstractSolrjIT;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SolrjLoaderSolrjIT extends AbstractSolrjIT {

    private CorporaWalkerFS corporaWalkerFS_Test;
    private SolrjLoader solrJListener;

    @Test
    public void testLoadAllTestCorpus() throws SolrServerException, IOException {
        runSolrjImporter();
        assertNumFoundAndReturnQueryResponse("id:*", 720);
    }

    @Ignore
    @Test
    public void testRunImporterForReal_IGNORE_THIS() throws SolrServerException {
        File rootDir = new File("/Users/arau/copilotCorpus/20150828/prod");
        assertTrue(rootDir.exists());
        corporaWalkerFS_Test = new CorporaWalkerFS(rootDir);
        solrJListener = new SolrjLoader(super.server, SolrjParams.FAST_INDEXING);
        Importer solrCopilotImporter = Importer.withCorporaWalker(corporaWalkerFS_Test).andListeners(solrJListener).build();
        solrCopilotImporter.run();
    }


}