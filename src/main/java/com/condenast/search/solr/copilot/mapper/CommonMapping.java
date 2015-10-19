package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 9/9/15.
 */
public class CommonMapping extends DocMapperBase {

    @Override
    public void map(CopilotDocument copilotDocument, CopilotDocument searchSchema, SolrInputDocument solrInputDocument) {
        solrInputDocument.addField("brandName_s", copilotDocument.brandName());
        successor(copilotDocument, searchSchema, solrInputDocument);
    }

}
