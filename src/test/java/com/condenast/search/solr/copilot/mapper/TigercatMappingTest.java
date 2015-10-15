package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.solr.copilot.mapper.tigercat.TigercatMapping;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TigercatMappingTest extends AbstractMappingTest {

    @Test
    public void testMap() {
        new TigercatMapping().map(copilotDocument, searchSchema, solrInputDocument);
        assertEquals(22, solrInputDocument.size());

        System.out.println(solrInputDocument);

        assertEquals("Hôtel du Cap-Eden-Roc", solrInputDocument.getFieldValue("name_s"));

        assertEquals(Arrays.asList("a", "b", "c"), solrInputDocument.getFieldValues("tags_ss"));

    }


}