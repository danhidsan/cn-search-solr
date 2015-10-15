package com.condenast.search.solr.copilot.indexer;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by arau on 9/10/15.
 */
public class SimpleCopilotImporterListener implements ImporterListener {

    List<SolrInputDocument> solrInputDocuments = new ArrayList<>();

    @Override
    public void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
        solrInputDocuments.add(solrInputDocument);
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

    }
}
