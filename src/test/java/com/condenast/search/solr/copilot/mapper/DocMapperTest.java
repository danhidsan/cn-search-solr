package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import com.condenast.search.solr.copilot.mapper.tigercat.TigercatMapping;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by arau on 9/8/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocMapperTest {

    private File anArticleFile;
    private CopilotDocument copilotDocument;
    private CopilotDocumentFile searchSchema;
    private SolrInputDocument solrInputDocument;

    @Before
    public void setup() throws IOException {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus//oneDocOneBrand/prod/cnt/articles/anArticle.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
        File searchSchemaFile = new File(dir.getPath() + "/copilotCorpus//oneDocOneBrand/prod/cnt/articles/searchSchema.json");
        searchSchema = new CopilotDocumentFile(searchSchemaFile);
        solrInputDocument = new SolrInputDocument();
    }

    @Test
    public void testTigercatMapping() throws IOException {
        new TigercatMapping().map(copilotDocument, searchSchema, solrInputDocument);
        assertNotNull(solrInputDocument);
        assertEquals(22, solrInputDocument.size());
    }

    @Test
    public void testMapCnOne() throws IOException {
        CnOneMapping.build().map(copilotDocument, searchSchema, solrInputDocument);
        assertNotNull(solrInputDocument);
        assertEquals(23, solrInputDocument.size());
        assertNotNull(solrInputDocument.get("brandName_s"));
        assertEquals("cnt", solrInputDocument.get("brandName_s").getValue());
    }


}
