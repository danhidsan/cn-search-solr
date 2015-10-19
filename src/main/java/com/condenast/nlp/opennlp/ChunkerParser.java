package com.condenast.nlp.opennlp;

import com.condenast.nlp.AnalysisContext;
import com.condenast.nlp.Analyzer;

import java.util.List;

/**
 * Created by arau on 10/16/15.
 */
public class ChunkerParser extends Analyzer {

    public ChunkerParser(AnalysisContext context) {
        super(context);
    }

    @Override
    public void analyze() {
//        FileInputStream chunkerStream = new FileInputStream(ModelUtil.fileOf("en-chunker.bin"));
//        ChunkerModel chunkerModel = new ChunkerModel(chunkerStream);
//        FileInputStream posStream = new FileInputStream(ModelUtil.fileOf("en-pos-maxent.bin"));
//        POSModel posModel = new POSModel(posStream);
//
//        ChunkerME chunker = new ChunkerME(chunkerModel);
//        POSTaggerME tagger = new POSTaggerME(posModel);
//        Parser parser = null;
//        try {
//            parser = new ChunkParser(chunker, tagger);
//        } catch (Exception e) {
//            throw new NLPException(e);
//        }
//        Parse[] results = ParserTool.parseLine("The Minnesota Twins , " + "the 1991 World Series Champions , are " +
//                "currently in third place.", parser, 1);
//        Parse p = results[0];
//        Parse[] chunks = p.getChildren();
//        assertEquals(8, chunks.length);
//        assertTrue(chunks[0].getType().equals("NP"));
//        assertTrue(chunks[0].getHead().toString().equals("Twins"));
//        //<end id="openChunkParse"/>
//
//        Arrays.asList(chunks).forEach(c -> {
//            if (c.getType().equals("NP")) {
//                System.out.println(c.getCoveredText());
//                System.out.println(c.getHead());
//                System.out.println(c.getLabel());
//                System.out.println(c.getProb());
//            }
//            System.out.println("-------");
//        });
//
//        Arrays.asList(chunks).forEach(Parse::show);
    }

    @Override
    public List<String> myTypes() {
        return null;
    }
}
