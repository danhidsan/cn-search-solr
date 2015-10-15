package com.condenast.search.solr.copilot.mapper.tigercat;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.solr.copilot.mapper.DocMapperBase;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 9/25/15.
 */
public class TigercatMapping extends DocMapperBase {

    @Override
    public void map(CopilotDocument copilotDocument, CopilotDocument searchSchema, SolrInputDocument solrInputDocument) {
        TigercatJsStub.mapSolrInputDocument(solrInputDocument, copilotDocument.toJson(), searchSchema.toJson());
        successor(copilotDocument, searchSchema, solrInputDocument);
    }

}
