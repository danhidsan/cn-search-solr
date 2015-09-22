package com.condenast.search.solr.copilot.mapper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GlobalMappingTest extends AbstractMappingTest {

    private GlobalMapping mapping;

    @Override
    public void setup() {
        super.setup();
        mapping = new GlobalMapping();
    }

    @Test
    public void testMap() throws Exception {
        mapping.map(copilotDocument, solrInputDocument);
        assertEquals(3, solrInputDocument.size());
    }
}