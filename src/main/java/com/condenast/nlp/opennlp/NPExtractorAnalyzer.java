package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;
import com.condenast.nlp.NLPException;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Span;

import java.util.List;

import static com.condenast.nlp.opennlp.SentenceDetectorAnalyzer.SENTENCE_TYPE;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by arau on 10/16/15.
 */
public class NPExtractorAnalyzer extends Analyzer {

    public static final String NP_ANNOTATION = "NP_TAG";
    public static final List<String> myTypes = unmodifiableList(asList(NP_ANNOTATION));
    public static final String EN_CHUNKER_MODEL_FILENAME = "en-chunker.bin";
    public static final String EN_POS_MAXENT_MODEL_FILENAME = "en-pos-maxent.bin";
    public static final String DETERMINER = "DT";
    public static final String NP_PARTS = "NP_PARTS";
    private int currentSentenceNr = 0;
    private List<String> sentences;
    private int currentOffset;

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
        Parse[] chunkParts = chunk.getChildren();
        int startIdx = 0;
        if (startsWithDeterminer(chunkParts)) startIdx = 1;
        int startOffset = currentOffset + chunkParts[startIdx].getSpan().getStart();
        int endOffset = currentOffset + chunkParts[chunk.getChildren().length - 1].getSpan().getEnd();
        Span span = new Span(startOffset, endOffset);
        context.addAnnotation(NP_ANNOTATION, span, chunk.getProb()).putFeature(NP_PARTS, chunkParts);
    }

    private boolean startsWithDeterminer(Parse[] parts) {
        return parts[0].getType().equals(DETERMINER);
    }

    @Override
    public List<String> myTypes() {
        return myTypes;
    }

}
