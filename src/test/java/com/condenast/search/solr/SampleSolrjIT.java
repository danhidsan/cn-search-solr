package com.condenast.search.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by arau on 9/14/15.
 */
public class SampleSolrjIT extends AbstractSolrjIT {

    @Test
    public void testSimple() throws SolrServerException, IOException {
        SolrInputDocument newDoc = new SolrInputDocument();
        newDoc.addField("title", "Test Document 1");
        newDoc.addField("id", "doc-1");
        newDoc.addField("body_en", "Hello world!");
        server.add(newDoc);
        server.commit();
        SolrDocumentList docList = assertNumFoundAndReturnQueryResponse("title:test", 1).getResults();
        SolrDocument doc = docList.get(0);
        System.out.println("Title: " + doc.getFirstValue("title").toString());
    }
}