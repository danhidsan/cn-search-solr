package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by arau on 9/9/15.
 */
public abstract class AbstractMappingTest {

    protected SearchSchemaMapping searchSchemaMapping;
    protected CopilotDocument copilotDocument;
    protected File searchSchemaFile;
    protected File anArticleFile;
    protected SolrInputDocument solrInputDocument = new SolrInputDocument();

    @Before
    public void setup() {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/anArticle.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
        searchSchemaFile = new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/searchSchema.json");
        assertTrue(searchSchemaFile.exists());
        assertTrue(anArticleFile.exists());
        searchSchemaMapping = new SearchSchemaMapping(searchSchemaFile);

    }
}
