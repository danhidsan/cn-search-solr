package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Created by arau on 9/9/15.
 */
public class CommonMapping implements DocMapping {

    public static final CommonMapping INSTANCE = new CommonMapping();

    @Override
    public void map(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
        solrInputDocument.addField("brandName_s", copilotDocument.brandName());
    }

}
