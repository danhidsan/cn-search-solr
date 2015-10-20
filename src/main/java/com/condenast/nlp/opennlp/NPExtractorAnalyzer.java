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
import java.util.stream.Collectors;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_TYPE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Created by arau on 10/16/15.
 */
public class NPExtractorAnalyzer extends Analyzer {

    public static final String NP_ANNOTATION = "NP_TAG";
    public static final String NP_LEMMATIZED_NGRAMS = "NP_LEMMATIZED_NGRAMS";
    public static final List<String> myTypes = unmodifiableList(asList(NP_ANNOTATION));
    public static final String EN_CHUNKER_MODEL_FILENAME = "en-chunker.bin";
    public static final String EN_POS_MAXENT_MODEL_FILENAME = "en-pos-maxent.bin";
    public static final String DETERMINER = "DT";
    public static final String NP_PARTS = "NP_PARTS";
    private int currentSentenceNr = 0;
    private List<String> sentences;
    private int currentOffset;
    SimpleLemmatizer lemmatizer = new SimpleLemmatizer();

    public NPExtractorAnalyzer(AnalysisContext context) {
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
        Parser parser = tryNewParser();
        Parse[] results = ParserTool.parseLine(currentSentence, parser, 1);
        asList(results[0].getChildren()).forEach(this::possiblyAddAnnotation);
        currentOffset = context.annotations(SENTENCE_TYPE).get(currentSentenceNr).getSpan().getEnd() + 1;
    }

    private Parser tryNewParser() {
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
        if (!chunk.getType().equals("NP")) return;

        List<Parse> chunkParts = new ArrayList(asList(chunk.getChildren()));
        if (isPronounOrDeterminer(chunkParts.get(0))) chunkParts.remove(0);
        if (chunkParts.isEmpty() || chunkParts.stream().filter(isNoun()).collect(toList()).isEmpty()) return;

        int startOffset = currentOffset + chunkParts.get(0).getSpan().getStart();
        int endOffset = currentOffset + chunkParts.get(chunkParts.size() - 1).getSpan().getEnd();
        Span span = new Span(startOffset, endOffset);
        Annotation npAnnotation = context.addAnnotation(NP_ANNOTATION, span, chunk.getProb());
        npAnnotation.putFeature(NP_PARTS, chunkParts);
        labelWithLemmas(npAnnotation);
        generateLemmatizedNGramsFeature(npAnnotation);
    }

    private void generateLemmatizedNGramsFeature(Annotation npAnnotation) {
        List<Parse> chunkParts = npPartsFeature(npAnnotation);
        final String lemmatizedChunkLabel = chunkParts.stream().map(Parse::getLabel).collect(Collectors.joining(" "));
        List<String> ngrams = NGramsHelper.generate(lemmatizedChunkLabel, Math.min(chunkParts.size(), 3), Integer.MAX_VALUE);
        npAnnotation.putFeature(NP_LEMMATIZED_NGRAMS, ngrams);
    }

    private boolean isPronounOrDeterminer(Parse chunkPart) {
        return (chunkPart.getType().equals(DETERMINER) ||
                chunkPart.getType().startsWith("P") ||
                chunkPart.getType().startsWith("W"));
    }

    private Predicate<? super Parse> isNoun() {
        return chunkPart -> chunkPart.getType().startsWith("N");
    }

    protected void labelWithLemmas(Annotation npAnnotation) {
        npPartsFeature(npAnnotation).forEach(p -> {
            String lemmatizedText = lemmatizer.lemmatize(p.getCoveredText(), p.getType());
            p.setLabel(lemmatizedText);
        });
    }

    private List<Parse> npPartsFeature(Annotation npAnnotation) {
        return ((List<Parse>) npAnnotation.getFeature(NP_PARTS));
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

}
