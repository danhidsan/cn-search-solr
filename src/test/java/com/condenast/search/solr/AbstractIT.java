package com.condenast.search.solr;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.CorporaWalker;
import com.condenast.search.corpus.utils.copilot.walker.fs.CorporaWalkerFS;
import com.condenast.search.solr.copilot.importer.Importer;
import com.condenast.search.solr.copilot.importer.ImporterListener;
import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.junit.experimental.categories.Category;
import org.noggit.ObjectBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;

import static com.condenast.search.solr.SolrConfigTestHelper.*;
import static java.lang.Integer.parseInt;

/**
 * Created by arau on 9/9/15.
 */
@Category(IntegrationTest.class)
public abstract class AbstractIT extends SolrTestCaseJ4 {

    protected static void jsonResponse() {
        lrf.args.put(CommonParams.WT, "json");
    }

    protected static void xmlResponse() {
        lrf.args.put(CommonParams.WT, "xml");
    }

    protected static void initCnOneEditIdx() throws Exception {
        initCore(cnOneEditConfigXml(), cnOneEditSchemaXml(), solrConfigURL().getPath(), CN_ONE_EDIT);
    }

    protected static void ensureIndexHasSomething() throws SolrServerException {
        if (numFound(queryAndResponseJsonLHM("id:*")) == 0) {
            runImporter(1);
        }
    }

    protected static void runImporter(int maxDocsToImportPerCollection) {
        CorporaWalker corporaWalker = new CorporaWalkerFS(testCopilotCorpus10DocsPerBrandPerCollectionRootDir());
        Importer copilotImporter = Importer.withCorporaWalker(corporaWalker).andListeners(ADocLoader.INSTANCE).andMaxDocs(maxDocsToImportPerCollection).build();
        copilotImporter.run();
    }

    static class ADocLoader implements ImporterListener {

        public final static ADocLoader INSTANCE = new ADocLoader();

        @Override
        public void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
            assertU(adoc(solrInputDocument));
        }

        @Override
        public void onError(Exception e, CopilotDocument currentCopilotDocument, SolrInputDocument currentSolrInputDocument) {
            throw new RuntimeException(e);
        }

        @Override
        public void onStart(Importer importer) {
        }

        @Override
        public void onEnd(Importer importer) {
            assertU(commit());
        }
    }

    protected static String queryAndJsonResponse(String query) {
        jsonResponse();
        try {
            return h.query(lrf.makeRequest(query));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected static JsonLHM queryAndResponseJsonLHM(String query) {
        String jsonResp = queryAndJsonResponse(query);
        log.info("JSON response for query: '" + query + "':\n\t\t>>>> " + jsonResp);
        return JsonLHM.fromJSON(jsonResp);
    }

    protected static void assertQJNumFound(String query, int expectedNum) {
        JsonLHM jsonLHM = queryAndResponseJsonLHM(query);
        assertEquals("query search numFound are not equals", expectedNum, numFound(jsonLHM));
    }

    static int numFound(JsonLHM rootJsonLHM) {
        return rootJsonLHM.obj("response").asInt("numFound");
    }


    public static class JsonLHM {

        private LinkedHashMap<String, Object> lhm;

        private JsonLHM(LinkedHashMap<String, Object> lhm) {
            this.lhm = lhm;
        }

        public static JsonLHM fromJSON(String json) {

            try {
                return new JsonLHM(json);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }

        }

        private JsonLHM(String json) throws IOException {
            this((LinkedHashMap<String, Object>) ObjectBuilder.fromJSON(json));
        }

        public String str(String key) {
            checkExists(key);
            return lhm.get(key).toString();
        }

        private void checkExists(String key) {
            if (!lhm.containsKey(key))
                throw new IllegalArgumentException("Key: '" + key + "' does not exist in lhm: \n" +
                        lhm.toString());
        }

        public JsonLHM obj(String key) {
            checkExists(key);
            return new JsonLHM((LinkedHashMap<String, Object>) lhm.get(key));
        }

        public Integer integer(String key) {
            checkExists(key);
            return parseInt(str(key));
        }

        public int asInt(String key) {
            return integer(key);
        }

    }

}
