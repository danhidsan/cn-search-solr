package com.condenast.search.solr.copilot.indexer;

import com.condenast.search.solr.AbstractSolrjIT;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;

public class SolrjLoaderSolrjIT extends AbstractSolrjIT {

    @Test
    public void testLoad1TestCorpus() throws SolrServerException, IOException {
        runSolrjImporter(1);
        assertNumFoundAndReturnQueryResponse("id:*", 77);
    }

}