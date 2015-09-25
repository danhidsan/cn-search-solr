package com.condenast.search.solr.copilot.mapper;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SearchSchemaMappingTest extends AbstractMappingTest {

    @Test
    public void testMap() {
        searchSchemaMapping.map(copilotDocument, solrInputDocument);
        assertEquals(22, solrInputDocument.size());

        System.out.println(solrInputDocument);

        assertEquals("Hôtel du Cap-Eden-Roc", solrInputDocument.getFieldValue("name_s"));

        assertEquals(Arrays.asList("a", "b", "c"), solrInputDocument.getFieldValues("tags_ss"));

    }


}