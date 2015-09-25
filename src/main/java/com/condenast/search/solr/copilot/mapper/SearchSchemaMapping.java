package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.solr.copilot.mapper.tigercat.TigercatJsStub;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;

import java.io.File;
import java.io.IOException;

/**
 * Created by arau on 9/25/15.
 */
public class SearchSchemaMapping implements DocMapping {

    private final String searchSchemaJson;

    public SearchSchemaMapping(String searchSchemaJson) {
        Validate.notEmpty(searchSchemaJson);
        this.searchSchemaJson = searchSchemaJson;
    }

    public SearchSchemaMapping(File searchSchemaFile) throws IOException {
        this(FileUtils.readFileToString(searchSchemaFile));
    }

    @Override
    public void map(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
        TigercatJsStub.mapSolrInputDocument(solrInputDocument, copilotDocument.toJson(), searchSchemaJson);
    }
}
