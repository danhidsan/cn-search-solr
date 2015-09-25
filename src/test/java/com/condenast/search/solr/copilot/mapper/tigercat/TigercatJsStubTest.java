package com.condenast.search.solr.copilot.mapper.tigercat;

import com.oracle.javafx.jmx.json.JSONException;
import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrInputDocument;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Created by arau on 9/23/15.
 */
public class TigercatJsStubTest {

    private static String anArticleJson;
    private static String anArticleSearchSchema;

    @BeforeClass
    public static void setup() throws IOException, JSONException {
        URL dir = TigercatJsStubTest.class.getResource("/");
        anArticleJson = FileUtils.readFileToString(new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/anArticle.json"));
        anArticleSearchSchema = FileUtils.readFileToString(new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/searchSchema.json"));
    }

    @Test
    public void testNodeSelector_Obj_Attr() {
        SolrInputDocument solrInputDocument = TigercatJsStub.toSolrDoc(anArticleJson, anArticleSearchSchema);
        assertEquals(22, solrInputDocument.size());
        assertEquals("53d9b2b8dcd5888e14594bb4", solrInputDocument.getField("id").getValue());
    }


}
