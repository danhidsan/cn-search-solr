/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.condenast.nlp.opennlp.lemmatizer;

import com.condenast.nlp.NLPException;
import opennlp.tools.util.StringUtil;
import org.apache.commons.lang.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static com.condenast.nlp.opennlp.ResourceUtil.dictionaryInputStreamOf;

public class SimpleLemmatizer implements DictionaryLemmatizer {

    public static final String EN_LEMMATIZER_TXT = "en-lemmatizer.txt";
    public final Set<String> constantTags = new HashSet<String>(Arrays.asList("NNP", "NP00000"));
    private HashMap<List<String>, String> dictMap;
    private static HashMap<String, HashMap<List<String>, String>> cacheDictMap = new HashMap<>();
    private static final List<Character> UNDESIRED = Arrays.asList(',', '.', ';', '?', '!', '%', '"');

    public SimpleLemmatizer() {
        this(EN_LEMMATIZER_TXT);
    }

    public SimpleLemmatizer(String dictionaryName) {
        loadDictionary(dictionaryName);
    }

    private synchronized void loadDictionary(String dictionaryName) {
        Validate.notEmpty(dictionaryName);
        if (cacheDictMap.containsKey(dictionaryName)) {
            dictMap = cacheDictMap.get(dictionaryName);
            return;
        }
        dictMap = new HashMap<>();
        BufferedReader breader = new BufferedReader(new InputStreamReader(dictionaryInputStreamOf(dictionaryName)));
        String line;
        try {
            while ((line = breader.readLine()) != null) {
                String[] elems = line.split("\t");
                dictMap.put(Arrays.asList(elems[0], elems[2]), elems[1]);
            }
        } catch (IOException e) {
            throw new NLPException(e);
        } finally {
            try {
                breader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cacheDictMap.put(dictionaryName, dictMap);
    }

    private List<String> getDictKeys(String word, String postag) {
        List<String> keys = new ArrayList<>();
        if (constantTags.contains(postag)) {
            keys.addAll(Arrays.asList(word, postag));
        } else {
            keys.addAll(Arrays.asList(StringUtil.toLowerCase(word), postag));
        }
        return keys;
    }

    public String lemmatize(final String word, final String postag) {
        //String normWord = normalize(word);
        String lemma;
        List<String> keys = getDictKeys(word, postag);
        String keyValue = dictMap.get(keys);
        if (keyValue != null) {
            lemma = keyValue;
        } else if (constantTags.contains(postag)) {
            lemma = word;
        } else if (word.toUpperCase().equals(word)) {
            lemma = word;
        } else {
            lemma = StringUtil.toLowerCase(word);
        }
        return lemma;
    }

    private String normalize(final String word) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!UNDESIRED.contains(chars[i])) stringBuilder.append(chars[i]);
        }
        return stringBuilder.toString();
    }

}

