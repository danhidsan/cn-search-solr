package com.condenast.search.solr;

import com.condenast.search.corpus.utils.copilot.walker.CorporaWalker;
import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.copilot.importer.Importer;
import com.condenast.search.solr.copilot.importer.SolrjLoader;
import com.condenast.search.solr.copilot.importer.SolrjParams;
import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.CoreContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

import static com.condenast.search.solr.SolrConfigTestHelper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by arau on 9/14/15.
 */
@Category(IntegrationTest.class)
public abstract class AbstractSolrjIT {

    protected static EmbeddedSolrServer server;
    protected static CoreContainer container;
    protected static File solrHome;

    @BeforeClass
    public static void setUp() throws Exception {
        solrHome = new File(solrConfigURL().getPath());
        assertTrue("solr-config should exists: " + solrHome.getAbsolutePath(), solrHome.exists());
        cleanIndex();
        container = new CoreContainer(solrHome.getAbsolutePath());
        container.load();
        server = new EmbeddedSolrServer(container, CN_ONE_EDIT);
    }

    protected void ensureIndexHasSomething() throws SolrServerException, IOException {
        if (queryResponse("id:*").getResults().getNumFound() == 0) {
            runSolrjImporter(1);
        }
    }

    protected static void cleanIndex() throws IOException {
        File idxHome = new File(solrHome, "/" + CN_ONE_EDIT + "/data/");
        FileUtils.deleteDirectory(idxHome);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.close();
    }

    public QueryResponse assertNumFoundAndReturnQueryResponse(String query, int num) throws SolrServerException, IOException {
        QueryResponse qResp = queryResponse(query);
        assertEquals(num, qResp.getResults().getNumFound());
        return qResp;
    }

    protected QueryResponse queryResponse(String query) throws SolrServerException, IOException {
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("q", query);
        return server.query(params);
    }

    protected void runSolrjImporter() {
        runSolrjImporter(Integer.MAX_VALUE);
    }

    protected void runSolrjImporter(int maxDocsPerCollection) {
        CorporaWalker corporaWalker = new CorporaWalkerFS(testCopilotCorpus10DocsPerBrandPerCollectionRootDir());
        SolrjLoader solrjLoader = new SolrjLoader(server, SolrjParams.FAST_INDEXING);
        Importer copilotImporter = Importer.withCorporaWalker(corporaWalker).andListeners(solrjLoader).andMaxDocs(maxDocsPerCollection).build();
        copilotImporter.run();
    }


}