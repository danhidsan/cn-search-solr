package com.condenast.search.solr;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.solr.copilot.indexer.Importer;
import com.condenast.search.solr.copilot.indexer.ImporterListener;
import org.apache.solr.common.SolrInputDocument;

import static org.apache.solr.SolrTestCaseJ4.*;

/**
 * Created by arau on 10/2/15.
 */
public class ADocLoader implements ImporterListener {

    public final static ADocLoader INSTANCE = new ADocLoader();

    @Override
    public void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
        assertU(adoc(solrInputDocument));
    }

    @Override
    public void onError(Exception e, CopilotDocument currentCopilotDocument, SolrInputDocument currentSolrInputDocument) {
        throw new RuntimeException(e);
    }

    @Override
    public void onStart(Importer importer) {
    }

    @Override
    public void onEnd(Importer importer) {
        assertU(commit());
    }
}



