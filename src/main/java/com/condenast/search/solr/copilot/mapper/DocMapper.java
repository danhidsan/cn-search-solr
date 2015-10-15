package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 9/8/15.
 */
public interface DocMapper {

    void map(final CopilotDocument copilotDocument, final CopilotDocument searchSchema, final SolrInputDocument solrInputDocument);

    void setSuccessor(DocMapper docMaping);

}
