package com.condenast.search.solr.copilot.mapper;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang.Validate;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.collections.ListUtils.unmodifiableList;

/**
 * Created by arau on 9/23/15.
 */
public class JsonSelect {

    public static final List<String> EMPTY_LIST = unmodifiableList(emptyList());
    private static ScriptEngine engine;
    private final String json;

    public JsonSelect(String json) {
        Validate.notEmpty(json);
        this.json = json;
    }

    private static Invocable jsonselect() {
        if (engine == null) {
            engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                URL jsonselect_js = JsonSelect.class.getResource("./jsonselect.js");
                engine.eval(new FileReader(jsonselect_js.getPath()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (Invocable) engine;
    }

    public List<String> strValues(String selector) {
        ScriptObjectMirror jsArray = tryInvokeJsonSelectJS(selector);
        if (jsArray == null) return EMPTY_LIST;
        return jsArray.values().stream().sorted().distinct().map(Object::toString).collect(Collectors.toList());
    }

    private ScriptObjectMirror tryInvokeJsonSelectJS(String selector) {
        try {
            return (ScriptObjectMirror) jsonselect().invokeFunction("jsonselect", json, selector);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
