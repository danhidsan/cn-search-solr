package com.condenast.search.solr.copilot.mapper;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CommonMappingTest extends AbstractMappingTest {

    private CommonMapping mapping;

    @Override
    public void setup() throws IOException {
        super.setup();
        mapping = new CommonMapping();
    }

    @Test
    public void testMap() throws Exception {
        mapping.map(copilotDocument, searchSchema, solrInputDocument);
        assertEquals(1, solrInputDocument.size());
        assertEquals("cnt", solrInputDocument.get("brandName_s").getValue());
    }
}