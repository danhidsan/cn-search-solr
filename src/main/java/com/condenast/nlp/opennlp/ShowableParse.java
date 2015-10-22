package com.condenast.nlp.opennlp;

import opennlp.tools.parser.Parse;
import opennlp.tools.util.Span;

/**
 * Created by arau on 10/22/15.
 */
public class ShowableParse extends Parse {

    public ShowableParse(Parse p) {
        this(p.getText(), p.getSpan(), p.getType(), p.getProb(), p);
    }

    private ShowableParse(String text, Span span, String type, double p, int index) {
        super(text, span, type, p, index);
    }

    private ShowableParse(String text, Span span, String type, double p, Parse h) {
        super(text, span, type, p, h);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        show(buffer);
        return buffer.toString();
    }
}
