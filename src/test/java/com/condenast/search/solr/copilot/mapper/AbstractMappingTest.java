package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.fs.CopilotDocumentFile;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

/**
 * Created by arau on 9/9/15.
 */
public abstract class AbstractMappingTest {

    protected CopilotDocument copilotDocument;
    protected File anArticleFile;
    protected SolrInputDocument solrInputDocument;
    protected CopilotDocument searchSchema;

    @Before
    public void setup() throws IOException {
        URL dir = this.getClass().getResource("/");
        anArticleFile = new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/anArticle.json");
        copilotDocument = new CopilotDocumentFile(anArticleFile);
        File searchSchemaFile = new File(dir.getPath() + "/copilotCorpus/oneDocOneBrand/prod/cnt/articles/searchSchema.json");
        assertTrue(searchSchemaFile.exists());
        assertTrue(anArticleFile.exists());
        searchSchema = new CopilotDocumentFile(searchSchemaFile);
        solrInputDocument = new SolrInputDocument();
    }
}
