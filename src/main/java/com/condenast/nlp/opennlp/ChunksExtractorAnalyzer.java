package com.condenast.nlp.opennlp;

import com.condenast.nlp.*;
import com.condenast.nlp.opennlp.lemmatizer.SimpleLemmatizer;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.condenast.nlp.opennlp.ResourceUtil.modelOf;
import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_ANNOTATION;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/16/15.
 */
public class ChunksExtractorAnalyzer extends Analyzer {

    public static final String NP_ANNOTATION = "NP_ANNOTATION";
    public static final String VP_ANNOTATION = "VP_ANNOTATION";
    public static final String LEMMATIZED_NGRAMS_FEATURE = "LEMMATIZED_NGRAMS";
    public static final String PARTS_FEATURE = "PARTS";

    public static final List<String> myTypes = unmodifiableList(asList(NP_ANNOTATION, VP_ANNOTATION));

    public static final String EN_CHUNKER_MODEL_FILENAME = "en-chunker.bin";
    public static final String EN_POS_MAXENT_MODEL_FILENAME = "en-pos-maxent.bin";
    public static final String DETERMINER = "DT";

    public static final int DEFAULT_MIN_NGRAMS_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAMS_SIZE = 5;
    public static final int DEFAULT_NUM_PARSES = 1;

    private int minNGramsSize = DEFAULT_MIN_NGRAMS_SIZE;
    private int maxNGramsSize = DEFAULT_MAX_NGRAMS_SIZE;

    private int currentSentenceNr = 0;
    private List<String> sentences;
    private int currentOffset;
    SimpleLemmatizer lemmatizer = new SimpleLemmatizer();

    public ChunksExtractorAnalyzer(AnalysisContext context) {
        super(context);
    }

    @Override
    public void analyze() {
        sentences = detectSentences();
        currentOffset = 0;
        for (currentSentenceNr = 0; currentSentenceNr < sentences.size(); currentSentenceNr++) {
            analyzeCurrentSentence();
            currentOffset = context.annotations(SENTENCE_ANNOTATION).get(currentSentenceNr).getSpan().getEnd() + 1;
        }
    }

    private void analyzeCurrentSentence() {
        String currentSentence = sentences.get(currentSentenceNr);
        ChunkParser parser = tryCreateParser();
        Parse[] results = parser.parseLine(currentSentence, DEFAULT_NUM_PARSES);
        asList(results[0].getChildren()).forEach(this::possiblyAddAnnotation);
    }

    private ChunkParser tryCreateParser() {
        ChunkerME chunker = new ChunkerME(modelOf(EN_CHUNKER_MODEL_FILENAME, ChunkerModel.class));
        POSTaggerME tagger = new POSTaggerME(modelOf(EN_POS_MAXENT_MODEL_FILENAME, POSModel.class));
        ChunkParser parser;
        try {
            parser = new ChunkParser(chunker, tagger);
        } catch (Exception e) {
            throw new NLPException(e);
        }
        return parser;
    }

    private void possiblyAddAnnotation(Parse chunk) {
        List<Parse> chunkParts = normalizeChunkIfNeeded(chunk);
        if (chunkParts.isEmpty()) return;
        Span span = determineChunkSpan(chunkParts);
        String annotationType = chunk.getType() + "_ANNOTATION";
        Annotation annotation = context.addAnnotation(annotationType, span, chunk.getProb());
        annotation.putFeature(PARTS_FEATURE, chunkParts);
        labelPartsWithLemmas(annotation);
        generateLemmatizedNGramsFeature(annotation);
    }

    private Span determineChunkSpan(List<Parse> npChunkParts) {
        Parse firstPOS = npChunkParts.get(0);
        int startOffset = currentOffset + firstPOS.getSpan().getStart();
        Parse lastPOS = npChunkParts.get(npChunkParts.size() - 1);
        int endOffset = currentOffset + lastPOS.getSpan().getEnd();
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
        int localMinNGramSize = Math.min(chunkParts.size(), minNGramsSize);
        int localMaxNGramSize = Math.max(minNGramsSize, maxNGramsSize);
        List<String> ngrams = NGramsHelper.generateNGramsFromChunking(chunkParts, localMinNGramSize, localMaxNGramSize);
        annotation.putFeature(LEMMATIZED_NGRAMS_FEATURE, ngrams);
    }

    private boolean isPronounOrDeterminer(Parse chunkPart) {
        return (chunkPart.getType().equals(DETERMINER) ||
                chunkPart.getType().startsWith("P") ||
                chunkPart.getType().startsWith("W"));
    }

    private Predicate<? super Parse> isNounOrVerb() {
        return chunkPart -> chunkPart.getType().startsWith("N") || chunkPart.getType().startsWith("V");
    }

    protected void labelPartsWithLemmas(Annotation annotation) {
        partsFeature(annotation).forEach(p -> {
            String lemmatizedText = lemmatizer.lemmatize(p.getCoveredText(), p.getType());
            p.setLabel(lemmatizedText);
        });
    }

    private List<Parse> partsFeature(Annotation npAnnotation) {
        return ((List<Parse>) npAnnotation.getFeature(PARTS_FEATURE));
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

    public int getMaxNGramsSize() {
        return maxNGramsSize;
    }

    public void setMaxNGramsSize(int maxNGramsSize) {
        Validate.isTrue(maxNGramsSize > 0, "maxNGramsSize must be > 0");
        Validate.isTrue(maxNGramsSize >= minNGramsSize, "maxNGramsSize must be >= minNGramsSize");
        this.maxNGramsSize = maxNGramsSize;
    }

    public int getMinNGramsSize() {
        return minNGramsSize;
    }

    public void setMinNGramsSize(int minNGramsSize) {
        Validate.isTrue(minNGramsSize > 0, "minNGramsSize must be > 0");
        this.minNGramsSize = minNGramsSize;
    }

}
