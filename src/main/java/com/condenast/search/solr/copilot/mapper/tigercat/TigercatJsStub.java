package com.condenast.search.solr.copilot.mapper.tigercat;

import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;
import org.noggit.ObjectBuilder;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by arau on 9/23/15.
 */
public class TigercatJsStub {

    private static ScriptEngine engine;

    private static Invocable tigercat() {
        if (engine == null) {
            tryInitNashorn();
        }
        return (Invocable) engine;
    }

    private static void tryInitNashorn() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            evalJavascript("./lodash.js");
            evalJavascript("./moment.js");
            evalJavascript("./converters.js");
            evalJavascript("./commonSchema.js");
            evalJavascript("./jsonselect.js");
            evalJavascript("./tigercat.js");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void evalJavascript(String filename) throws ScriptException, FileNotFoundException {
        URL js = TigercatJsStub.class.getResource(filename);
        engine.eval(new FileReader(js.getPath()));
    }

    public static void mapSolrInputDocument(SolrInputDocument solrInputDocument, String copilotDocJson, String searchSchemaJson) {
        Validate.notEmpty(copilotDocJson);
        Validate.notEmpty(searchSchemaJson);
        Validate.notNull(solrInputDocument);
        try {
            Object solrDocJson = tigercat().invokeFunction("toSolrDoc", copilotDocJson, searchSchemaJson);
            if (solrDocJson == null) throw new RuntimeException("Null solrDoc not expected for: " + copilotDocJson);
            if (solrDocJson instanceof String) {
                LinkedHashMap<String, Object> jsonObj = (LinkedHashMap<String, Object>) ObjectBuilder.fromJSON((String) solrDocJson);
                populate(solrInputDocument, jsonObj);
            } else {
                throw new UnsupportedOperationException("Unsupported tigercat returned type:" + solrDocJson.getClass());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void populate(SolrInputDocument solrInputDocument, LinkedHashMap<String, Object> jsonObj) {
        jsonObj.forEach((k, v) -> {
            if (v instanceof List && ((List) v).size() == 1) {
                solrInputDocument.addField(k, ((List) v).get(0));
            } else {
                solrInputDocument.addField(k, v);
            }
        });
    }

    protected static SolrInputDocument toSolrDoc(String copilotDocJson, String searchSchemaJson) {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        mapSolrInputDocument(solrInputDocument, copilotDocJson, searchSchemaJson);
        return solrInputDocument;
    }

}
