package com.condenast.nlp;

/**
 * Created by arau on 10/15/15.
 */
public class NLPException extends RuntimeException {
    public NLPException(Exception e) {
        super(e);
    }

    public NLPException(String s) {
        super(s);
    }
}
