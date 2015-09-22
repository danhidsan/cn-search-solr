package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by arau on 9/8/15.
 */
public class DocMapper {

    private final CopilotDocument copilotDocument;

    private final List<DocMapping> mappings = new ArrayList<DocMapping>();

    public static SolrInputDocument map(CopilotDocument copilotDocument, DocMapping... copilotSolrDocMappings) {
        return new DocMapper(copilotDocument).addMappings(copilotSolrDocMappings).map();
    }

    public DocMapper(CopilotDocument copilotDocument) {
        Validate.notNull(copilotDocument, "copilotDocument cannot be null");
        Validate.notEmpty(copilotDocument.toJson(), "json cannot be empty for copilotDocument.id=" + copilotDocument.id
                ());
        this.copilotDocument = copilotDocument;
    }

    public CopilotDocument source() {
        return this.copilotDocument;
    }

    public DocMapper addMappings(DocMapping... copilotSolrDocMappings) {
        Validate.notEmpty(copilotSolrDocMappings, "mappings cannot be empty");
        mappings.addAll(Arrays.asList(copilotSolrDocMappings));
        return this;
    }

    public List<DocMapping> mappings() {
        return Collections.unmodifiableList(this.mappings);
    }

    public SolrInputDocument map() {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        for (DocMapping mapping : this.mappings) {
            mapping.map(copilotDocument, solrInputDocument);
        }
        return solrInputDocument;
    }
}
