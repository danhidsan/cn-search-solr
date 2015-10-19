package com.condenast.nlp.opennlp;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ModelUtilTest {

    @Test
    public void testLoad() throws Exception {

        File file = ModelUtil.fileOf("en-sent.bin");
        Assert.assertTrue(file.exists());

    }
}