package com.condenast.nlp.opennlp;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ResourceUtilTest {

    @Test
    public void testBratDirPathExists() throws Exception {
        assertTrue(ResourceUtil.bratDirPath().toFile().exists());
    }
}