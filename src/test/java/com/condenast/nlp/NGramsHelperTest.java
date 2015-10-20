package com.condenast.nlp;

import com.condenast.nlp.NGramsHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class NGramsHelperTest {

    @Test
    public void testGenerateTriGram() throws Exception {

        String test = "former first lady Nancy Reagan";
        List<String> ngrams = NGramsHelper.generate(test, 3, Integer.MAX_VALUE);
        System.out.println(ngrams);
        assertEquals(6, ngrams.size());
        List<String> expectedNgrams = Arrays.asList("former first lady", "first lady Nancy", "former first lady Nancy", "lady Nancy Reagan", "first lady Nancy Reagan", "former first lady Nancy Reagan");
        AtomicInteger idx = new AtomicInteger();
        expectedNgrams.forEach(expectedNgram -> assertEquals(expectedNgram, ngrams.get(idx.getAndIncrement())));
    }

}