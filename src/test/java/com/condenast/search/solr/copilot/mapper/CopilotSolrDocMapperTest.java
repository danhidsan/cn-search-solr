package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
public class CopilotSolrDocMapperTest {

    private SearchSchemaMapping searchSchemaMapping;
    private File searchSchemaFile;
    private File anArticleFile;
    private CopilotDocument copilotDocument;

    @Before
    public void setup() throws IOException {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus//oneDocOneBrand/prod/cnt/articles/anArticle.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
        searchSchemaFile = new File(dir.getPath() + "/copilotCorpus//oneDocOneBrand/prod/cnt/articles/searchSchema.json");
        searchSchemaMapping = new SearchSchemaMapping(searchSchemaFile);
    }

    @Test
    public void testSource() {
        DocMapper mapper = new DocMapper(copilotDocument);
        assertNotNull(mapper.source());
        assertEquals(copilotDocument, mapper.source());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSourceNotNull() {
        new DocMapper(null);
    }

    @Test
    public void testAddMappings() {
        DocMapper mapper = new DocMapper(copilotDocument);
        DocMapping mockMapping = Mockito.mock(DocMapping.class);
        DocMapper mapper1 = mapper.addMappings(mockMapping);
        assertNotNull(mapper1);
        assertEquals(mapper, mapper1);

        assertNotNull(mapper.mappings());
        assertEquals(1, mapper.mappings().size());
    }

    @Test
    public void testMapOnlySearchMappings() throws IOException {
        DocMapper mapper = new DocMapper(copilotDocument);
        SearchSchemaMapping searchSchemaMapping = new SearchSchemaMapping(searchSchemaFile);
        SolrInputDocument solrInputDocument = mapper.addMappings(searchSchemaMapping).map();
        assertNotNull(solrInputDocument);
        assertEquals(22, solrInputDocument.size());
    }

    @Test
    public void testMapWithAll() throws IOException {
        DocMapper mapper = new DocMapper(copilotDocument);
        SearchSchemaMapping searchSchemaMapping = new SearchSchemaMapping(searchSchemaFile);
        SolrInputDocument solrInputDocument = mapper.addMappings(searchSchemaMapping, new CommonMapping()).map();
        assertNotNull(solrInputDocument);
        assertEquals(23, solrInputDocument.size());
    }


}
