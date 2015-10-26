package com.condenast.nlp.opennlp;

import opennlp.tools.parser.Parse;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChunkParserTest {

    private ChunkParser chunkParser;

    @Before
    public void setUp() throws Exception {
        chunkParser = new ChunkParser();
    }

    @Test
    public void testTokenizeShouldNotSeparatePossessiveAndAggregatePunctuation() throws Exception {
        Parse parent = chunkParser.tokenize("For better POS, This should not separate possessive's and tokenize " + "punctutation with last word.");
        assertEquals(16, parent.getChildCount());

        Parse possessive = parent.getChildren()[8];
        assertEquals("possessive's", possessive.getCoveredText());

        Parse lastOne = parent.getChildren()[15];
        assertEquals(".", lastOne.getCoveredText());
    }

    @Test
    public void testTokenizeShouldNotRemoveHyphen() throws Exception {
        Parse parent = chunkParser.tokenize("For better POS, This should not remove stuff-like hyphens.");
        assertEquals(11, parent.getChildCount());

        Parse hyphen = parent.getChildren()[8];
        assertEquals("stuff-like", hyphen.getCoveredText());

        Parse lastOne = parent.getChildren()[10];
        assertEquals(".", lastOne.getCoveredText());
    }

    @Test
    public void testOffsetIssueIfLastIsApos() throws Exception {
        Parse parent = chunkParser.tokenize("“Something would rip, and I’d say, ‘Just wait.’");
        assertEquals(13, parent.getChildCount());
        Parse last = parent.getChildren()[12];
        assertEquals("’", last.getCoveredText());
    }


}