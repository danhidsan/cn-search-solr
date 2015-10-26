/*
 * Copyright 2008-2011 Grant Ingersoll, Thomas Morton and Drew Farris
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -------------------
 * To purchase or learn more about Taming Text, by Grant Ingersoll, Thomas Morton and Drew Farris, visit
 * http://www.manning.com/ingersoll
 */

package com.condenast.nlp.opennlp;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.condenast.nlp.opennlp.ResourceUtil.EN_TOKEN_MODEL_BIN;
import static com.condenast.nlp.opennlp.ResourceUtil.modelOf;
import static java.util.Arrays.asList;

/**
 * Finds flat chunks instead of a tree structure using a simpler model.
 * <p>
 * This class is not thread-safe, but should be lightweight to construct.
 */
public class ChunkParser implements Parser {

    private ChunkerME chunker;
    private POSTaggerME tagger;
    public static final List<String> POSSESSIVES = asList("'s", "’s");
    public static final List<String> APOS = asList("'", "’");

    protected ChunkParser() {
    }

    public ChunkParser(ChunkerME chunker, POSTaggerME tagger) {
        this.chunker = chunker;
        this.tagger = tagger;
    }

    @Override
    public Parse parse(Parse tokens) {
        Parse[] children = tokens.getChildren();
        String[] words = new String[children.length];
        double[] probs = new double[words.length];
        for (int i = 0, il = children.length; i < il; i++) {
            words[i] = children[i].toString();
        }
        String[] tags = tagger.tag(words);
        tagger.probs(probs);
        for (int j = 0; j < words.length; j++) {
            Parse word = children[j];
            double prob = probs[j];
            tokens.insert(new Parse(word.getText(), word.getSpan(), tags[j], prob, j));
            tokens.addProb(Math.log(prob));
        }
        String[] chunks = chunker.chunk(words, tags);
        chunker.probs(probs);
        int chunkStart = -1;
        String chunkType = null;
        double logProb = 0;
        for (int ci = 0, cn = chunks.length; ci < cn; ci++) {
            if (ci > 0 && !chunks[ci].startsWith("I-") && !chunks[ci - 1].equals("O")) {
                Span span = new Span(children[chunkStart].getSpan().getStart(), children[ci - 1].getSpan().getEnd());
                tokens.insert(new Parse(tokens.getText(), span, chunkType, Math.exp(logProb), children[ci - 1]));
                logProb = 0;
            }
            if (chunks[ci].startsWith("B-")) {
                chunkStart = ci;
                chunkType = chunks[ci].substring(2);
            }
            logProb += Math.log(probs[ci]);
        }
        if (!chunks[chunks.length - 1].equals("O")) {
            int ci = chunks.length;
            Span span = new Span(children[chunkStart].getSpan().getStart(), children[ci - 1].getSpan().getEnd());
            tokens.insert(new Parse(tokens.getText(), span, chunkType, Math.exp(logProb), children[ci - 1]));
        }
        return tokens;
    }

    @Override
    public Parse[] parse(Parse tokens, int numParses) {
        //TODO: get multiple tag sequences and chunk each.
        return new Parse[]{parse(tokens)};
    }

    public Parse[] parseLine(final String line, int numParses) {
        Parse parent = tokenize(line);
        Parse[] parses;
        if (numParses == 1) {
            parses = new Parse[]{this.parse(parent)};
        } else {
            parses = this.parse(parent, numParses);
        }
        return parses;
    }

    protected Parse tokenize(String line) {
        TokenizerME tokenizerME = new TokenizerME(modelOf(EN_TOKEN_MODEL_BIN, TokenizerModel.class));
        Span[] tokenSpan = tokenizerME.tokenizePos(line);
        List<Span> normTokenSpan = reuniteApos_s_Tokens(tokenSpan, Span.spansToStrings(tokenSpan, line));
        Parse parent = new Parse(line, new Span(0, line.length()), AbstractBottomUpParser.INC_NODE, 0, 0);
        AtomicInteger counter = new AtomicInteger();
        normTokenSpan.forEach(ts -> {
            Parse constituent = new Parse(line, ts, AbstractBottomUpParser.TOK_NODE, 0, counter.getAndIncrement());
            parent.insert(constituent);
        });
        return parent;
    }

    private List<Span> reuniteApos_s_Tokens(Span[] tokenSpan, String[] tokenSpanText) {
        List<Span> normTokenSpan = new ArrayList<>();
        for (int i = 0; i < tokenSpan.length; i++) {
            Span normSpan = tokenSpan[i];
            String spanText = tokenSpanText[i];
            boolean isPossessiveThisToken = POSSESSIVES.contains(spanText.toLowerCase());
            boolean isPossessiveThisAndNextToken = APOS.contains(spanText.toLowerCase()) && (i + 1 < tokenSpan.length) && tokenSpanText[i + 1].equalsIgnoreCase("s");
            if (i > 0 && isPossessiveThisToken || isPossessiveThisAndNextToken) {
                normSpan = new Span(tokenSpan[i - 1].getStart(), tokenSpan[i - 1].getEnd() + 2);
                normTokenSpan.remove(normTokenSpan.size() - 1);
                if (isPossessiveThisAndNextToken) {
                    i++;
                }
            }
            normTokenSpan.add(normSpan);
        }
        return normTokenSpan;
    }


}
