package com.condenast.nlp.opennlp;

import com.condenast.nlp.NLPException;
import opennlp.tools.util.model.BaseModel;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arau on 10/15/15.
 */
public class ModelUtil {

    private final static Map<String, BaseModel> modelCache = new HashMap<>();

    public static URL modelDirURL() {
        return ModelUtil.class.getResource("./model/");
    }

    public static File fileOf(String name) {
        File file = new File(modelDirURL().getPath(), name);
        Validate.isTrue(file.exists(), "Cannot find model: " + name + " in " + modelDirURL().getPath());
        return file;
    }

    public static <T extends BaseModel> T modelFor(String modelFileName, Class<T> modelClass) {
        Validate.notEmpty(modelFileName);
        Validate.notNull(modelClass);
        if (!modelCache.containsKey(modelFileName)) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(ModelUtil.fileOf(modelFileName));
                BaseModel model = modelClass.getConstructor(InputStream.class).newInstance(stream);
                modelCache.put(modelFileName, model);
            } catch (Exception e) {
                throw new NLPException(e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return (T) modelCache.get(modelFileName);
    }

}
