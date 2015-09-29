package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.IntegrationTest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

import static com.condenast.search.solr.SolrConfigTestHelper.CN_ONE_EDIT;

@Ignore
@Category(IntegrationTest.class)
public class IGNORE_ME_IndexForRealIT {

    private CorporaWalkerFS corporaWalkerFS_Test;
    private SolrjLoader solrJListener;

    // Using this as a shortcut to create the real unified index meanwhile we build a cmd line importer
    @Test
    public void testRunImporterForReal_IGNORE_THIS() throws SolrServerException, IOException {

        EmbeddedSolrServer server = null;
        try {

            CoreContainer container = new CoreContainer(new File("/Users/arau/copilotCorpus/20150923/solr-config").getAbsolutePath());
            container.load();
            server = new EmbeddedSolrServer(container, CN_ONE_EDIT);

            File realCorpusDir = new File("/Users/arau/copilotCorpus/20150923/prod/vf");
            corporaWalkerFS_Test = new CorporaWalkerFS(realCorpusDir);
            solrJListener = new SolrjLoader(server, SolrjParams.FAST_INDEXING);
            Importer solrCopilotImporter = Importer.withCorporaWalker(corporaWalkerFS_Test).andListeners(solrJListener).build();
            solrCopilotImporter.run();
        } finally {
            if (server != null) server.close();
        }


    }


}