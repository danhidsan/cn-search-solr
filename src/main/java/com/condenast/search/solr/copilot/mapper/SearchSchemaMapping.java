package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.gistlabs.mechanize.document.json.node.JsonNode;
import com.gistlabs.mechanize.document.json.node.impl.ObjectNodeImpl;
import com.gistlabs.mechanize.util.css_query.NodeSelector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONException;
import org.json.JSONObject;
import org.noggit.ObjectBuilder;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by arau on 9/8/15.
 */
public class SearchSchemaMapping implements DocMapping {

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(SearchSchemaMapping.class);

    private LinkedHashMap<String, Object> searchSchemaMap;
    private Set<String> searchSchemaSolrFields;

    public SearchSchemaMapping(String searchSchemaJson) {
        parseSearchSchemaJson(searchSchemaJson);
    }

    public SearchSchemaMapping(File searchSchemaFile) {
        String searchSchemaJson;
        try {
            searchSchemaJson = FileUtils.readFileToString(searchSchemaFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read Search Schema: " + searchSchemaFile.getAbsolutePath(), e);
        }
        parseSearchSchemaJson(searchSchemaJson);
    }

    private void parseSearchSchemaJson(String searchSchemaJson) {
        Validate.notEmpty(searchSchemaJson, "searchSchemaJson cannot be empty");
        try {
            searchSchemaMap = (LinkedHashMap<String, Object>) ObjectBuilder.fromJSON(searchSchemaJson);
        } catch (IOException e) {
            throw new IllegalArgumentException("Search Schema json is not valid: " + searchSchemaJson, e);
        }
        searchSchemaSolrFields = searchSchemaMap.keySet();
    }

    protected NodeSelector<JsonNode> build(final String json) throws JSONException {
        Validate.notEmpty(json, "copilot doc json cannot be empty");
        ObjectNodeImpl node = new ObjectNodeImpl(new JSONObject(json));
        return node.buildNodeSelector();
    }

    @Override
    public void map(final CopilotDocument copilotDocument, final SolrInputDocument solrInputDocument) {
        Validate.notNull(copilotDocument, "copilotDocument cannot be null");
        Validate.notNull(solrInputDocument, "solrInputDocument cannot be null");
        NodeSelector<JsonNode> copilotDocNodeSelector = tryBuildSelector(copilotDocument);
        for (String solrFieldName : searchSchemaSolrFields) {
            String copilotDocJsonSelector = searchSchemaMap.get(solrFieldName).toString();
            if (!isModelSelector(copilotDocJsonSelector)) continue;
            String[] values = extractValues(copilotDocNodeSelector, copilotDocJsonSelector);
            if (isEmpty(values)) {
                logInfoEmptySelectorResult(copilotDocument, solrFieldName);
            } else {
                addSolrDocFields(solrInputDocument, solrFieldName, values);
            }
        }
    }

    private void addSolrDocFields(SolrInputDocument solrInputDocument, String solrFieldName, String[] values) {
        for (String value : values) {
            if (StringUtils.isBlank(value)) continue;
            solrInputDocument.addField(solrFieldName, value);
        }
    }

    private void logInfoEmptySelectorResult(CopilotDocument copilotDocument, String solrFieldName) {
        logger.info("selector: " + searchSchemaMap.get(solrFieldName).toString() + " returned no " +
                "values " + "for " + "copilotDoc:" + copilotDocument.uri());
    }

    private boolean isEmpty(String[] values) {
        return values == null || values.length == 0;
    }

    private NodeSelector<JsonNode> tryBuildSelector(CopilotDocument copilotDocument) {
        try {
            return build(copilotDocument.toJson());
        } catch (JSONException e) {
            throw new IllegalArgumentException("Copilot document: " + copilotDocument.id() + " contains not well " +
                    "formatted json: " + copilotDocument.toJson(), e);
        }
    }

    private String[] extractValues(NodeSelector<JsonNode> selector, String copilotDocJsonSelector) {
        String[] ret = null;
        String sel = copilotDocJsonSelector.replaceAll("\\.model", ":root").replaceAll("\\.", "");
        List<JsonNode> values = selector.findAll(sel);
        if (values != null) {
            ret = new String[values.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = values.get(i).getValue();
            }
        }
        return ret;
    }

    private boolean isModelSelector(String copilotDocJsonSelector) {
        return copilotDocJsonSelector != null && copilotDocJsonSelector.startsWith(".model");
    }


}
