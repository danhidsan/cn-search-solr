package com.condenast.nlp.opennlp;

import org.apache.commons.lang.Validate;

import java.io.File;
import java.net.URL;

/**
 * Created by arau on 10/15/15.
 */
public class ModelUtil {

    public static URL modelURL() {
        return ModelUtil.class.getResource("./model/");
    }

    public static File fileOf(String name) {
        File file = new File(modelURL().getPath(), name);
        Validate.isTrue(file.exists(), "Cannot find model: " + name + " in " + modelURL().getPath());
        return file;
    }

}
