package com.condenast.search.solr.copilot.mapper;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by arau on 9/23/15.
 */
public class JsonSelectTest {

    private static JsonSelect anArticleJsonSelect;

    @BeforeClass
    public static void setup() throws IOException, JSONException {
        URL dir = JsonSelectTest.class.getResource("/");
        String json = FileUtils.readFileToString(new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/anArticle.json"));
        anArticleJsonSelect = new JsonSelect(json);
    }

    @Test
    public void testNodeSelector_Obj_Attr() throws JSONException {
        List<String> strVals = anArticleJsonSelect.strValues(".model > .name");
        assertEquals(1, strVals.size());
        assertEquals("Hôtel du Cap-Eden-Roc", strVals.get(0));
    }

    @Test
    public void testNodeSelector_Obj_Array() throws JSONException {
        List<String> strVals = anArticleJsonSelect.strValues(".model > .tags *");
        assertEquals(3, strVals.size());
        assertEquals("a", strVals.get(0));
        assertEquals("b", strVals.get(1));
        assertEquals("c", strVals.get(2));
    }

    @Test
    public void testNodeSelector_Obj_NestedObj() throws JSONException {
        List<String> strVals = anArticleJsonSelect.strValues(".rels > .contributorsAuthor .name");
        assertEquals(1, strVals.size());
        assertEquals("Julie L. Belcove", strVals.get(0));
    }

    @Test
    public void testNodeSelector_3Levels() throws JSONException {
        List<String> strVals = anArticleJsonSelect.strValues(".model > .address > .city");
        assertEquals(1, strVals.size());
        assertEquals("Cap d'Antibes", strVals.get(0));
    }

    @Test
    public void testNodeSelector_has_val() throws JSONException {
        List<String> strVals = anArticleJsonSelect.strValues(".model .meta:has( .modelName:val(\"hotel\") ) > " + ".modelName");
        assertEquals(1, strVals.size());
        assertEquals("hotel", strVals.get(0));
    }


}
