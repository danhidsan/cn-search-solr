package com.condenast.search.solr.copilot.mapper;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SearchSchemaMappingTest extends AbstractMappingTest {

    @Test
    public void testMap() throws JSONException, IOException {
        searchSchemaMapping.map(copilotDocument, solrInputDocument);
        assertEquals(10, solrInputDocument.size());

        assertEquals("Recipe: Vegan Anjeer Barfi to Celebrate Diwali", solrInputDocument.getFieldValue("hed_en"));

        assertEquals(Arrays.asList("secondaryChannels1", "secondaryChannels2"), solrInputDocument.getFieldValues("secondaryChannels_ss"));

    }


}