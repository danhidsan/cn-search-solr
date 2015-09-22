package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 9/10/15.
 */
public interface ImporterListener {
    void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument);

    void onError(Exception e, CopilotDocument currentCopilotDocument, SolrInputDocument currentSolrInputDocument);

    void onStart(Importer importer);

    void onEnd(Importer importer);
}
