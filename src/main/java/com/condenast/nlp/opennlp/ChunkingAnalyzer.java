package com.condenast.nlp.opennlp;

import com.condenast.nlp.*;
import com.condenast.nlp.opennlp.lemmatizer.SimpleLemmatizer;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_ANNOTATION;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/16/15.
 */
public class ChunkingAnalyzer extends Analyzer {

    public static final String NP_ANNOTATION = "NP_ANNOTATION";
    public static final String VP_ANNOTATION = "VP_ANNOTATION";
    public static final String LEMMATIZED_NGRAMS = "LEMMATIZED_NGRAMS";
    public static final List<String> myTypes = unmodifiableList(asList(NP_ANNOTATION, VP_ANNOTATION));
    public static final String EN_CHUNKER_MODEL_FILENAME = "en-chunker.bin";
    public static final String EN_POS_MAXENT_MODEL_FILENAME = "en-pos-maxent.bin";
    public static final String DETERMINER = "DT";
    public static final String PARTS = "PARTS";
    public static final int MIN_LEMMATIZED_NGRAMS_SIZE = 3;
    private int currentSentenceNr = 0;
    private List<String> sentences;
    private int currentOffset;
    SimpleLemmatizer lemmatizer = new SimpleLemmatizer();

    public ChunkingAnalyzer(AnalysisContext context) {
        super(context);
    }

    @Override
    public void analyze() {
        sentences = detectSentences();
        currentOffset = 0;
        for (currentSentenceNr = 0; currentSentenceNr < sentences.size(); currentSentenceNr++) {
            analyzeCurrentSentence();
        }
    }

    private void analyzeCurrentSentence() {
        String currentSentence = sentences.get(currentSentenceNr);
        Parser parser = tryCreateParser();
        Parse[] results = ParserTool.parseLine(currentSentence, parser, 1);
        asList(results[0].getChildren()).forEach(this::possiblyAddAnnotation);
        currentOffset = context.annotations(SENTENCE_ANNOTATION).get(currentSentenceNr).getSpan().getEnd() + 1;
    }

    private Parser tryCreateParser() {
        ChunkerME chunker = new ChunkerME(ResourceUtil.modelOf(EN_CHUNKER_MODEL_FILENAME, ChunkerModel.class));
        POSTaggerME tagger = new POSTaggerME(ResourceUtil.modelOf(EN_POS_MAXENT_MODEL_FILENAME, POSModel.class));
        Parser parser;
        try {
            parser = new ChunkParser(chunker, tagger);
        } catch (Exception e) {
            throw new NLPException(e);
        }
        return parser;
    }

    private void possiblyAddAnnotation(Parse chunk) {
        if (notNounPhrase(chunk) && notVerbalPhrase(chunk)) return;
        List<Parse> chunkParts = normalizeChunkIfNeeded(chunk);
        if (chunkParts.isEmpty() || chunkParts.stream().filter(isNounOrVerb()).collect(toList()).isEmpty()) return;
        Span span = determineChunkSpan(chunkParts);
        String annotationType = notNounPhrase(chunk) ? VP_ANNOTATION : NP_ANNOTATION;
        Annotation annotation = context.addAnnotation(annotationType, span, chunk.getProb());
        annotation.putFeature(PARTS, chunkParts);
        labelWithLemmas(annotation);
        generateLemmatizedNGramsFeature(annotation);
    }

    private Span determineChunkSpan(List<Parse> npChunkParts) {
        int startOffset = currentOffset + npChunkParts.get(0).getSpan().getStart();
        int endOffset = currentOffset + npChunkParts.get(npChunkParts.size() - 1).getSpan().getEnd();
        return new Span(startOffset, endOffset);
    }

    private List<Parse> normalizeChunkIfNeeded(Parse chunk) {
        List<Parse> chunkParts = new ArrayList(asList(chunk.getChildren()));
        if (isPronounOrDeterminer(chunkParts.get(0))) chunkParts.remove(0);
        return chunkParts.stream().map(ShowableParse::new).collect(toList());
    }

    private boolean notNounPhrase(Parse chunk) {
        return !chunk.getType().equals("NP");
    }

    private boolean notVerbalPhrase(Parse chunk) {
        return !chunk.getType().equals("VP");
    }

    private void generateLemmatizedNGramsFeature(Annotation annotation) {
        List<Parse> chunkParts = partsFeature(annotation);
        List<String> ngrams = NGramsHelper.generate(annotation.text(), Math.min(chunkParts.size(), MIN_LEMMATIZED_NGRAMS_SIZE), Integer.MAX_VALUE);
        annotation.putFeature(LEMMATIZED_NGRAMS, ngrams);
    }

    private boolean isPronounOrDeterminer(Parse chunkPart) {
        return (chunkPart.getType().equals(DETERMINER) ||
                chunkPart.getType().startsWith("P") ||
                chunkPart.getType().startsWith("W"));
    }

    private Predicate<? super Parse> isNounOrVerb() {
        return chunkPart -> chunkPart.getType().startsWith("N") || chunkPart.getType().startsWith("V");
    }

    protected void labelWithLemmas(Annotation annotation) {
        partsFeature(annotation).forEach(p -> {
            String lemmatizedText = lemmatizer.lemmatize(p.getCoveredText(), p.getType());
            p.setLabel(lemmatizedText);
        });
    }

    private List<Parse> partsFeature(Annotation npAnnotation) {
        return ((List<Parse>) npAnnotation.getFeature(PARTS));
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

}
