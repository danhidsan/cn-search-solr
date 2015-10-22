package com.condenast.nlp;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arau on 10/21/15.
 */
public class Annotations {

    public static String toBratFormat(List<Annotation> annotations) {
        StringBuilder builder = new StringBuilder();
        AtomicInteger id = new AtomicInteger(1);
        annotations.stream().forEach(a -> builder.append(a.toBratFormat(id)).append("\n"));
        return builder.toString();
    }

}
