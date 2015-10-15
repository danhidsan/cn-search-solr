package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 10/13/15.
 */
public abstract class DocMapperBase implements DocMapper {

    protected DocMapper successor;

    @Override
    public void setSuccessor(DocMapper docMaping) {
        this.successor = docMaping;
    }

    protected void successor(CopilotDocument copilotDocument, CopilotDocument searchSchema, SolrInputDocument solrInputDocument) {
        if (successor != null) successor.map(copilotDocument, searchSchema, solrInputDocument);
    }

    public static DocMapper assembleChain(DocMapper... docMappers) {
        Validate.notEmpty(docMappers);
        if (docMappers.length == 1) return docMappers[0];
        DocMapper prev = null;
        for (int i = docMappers.length - 1; i >= 0; i--) {
            docMappers[i].setSuccessor(prev);
            prev = docMappers[i];
        }
        return docMappers[0];
    }

}
