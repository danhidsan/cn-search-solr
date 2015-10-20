package com.condenast.nlp.opennlp;

import com.condenast.nlp.NLPException;
import opennlp.tools.util.model.BaseModel;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arau on 10/15/15.
 */
public class ResourceUtil {

    private final static Map<String, BaseModel> modelCache = new HashMap<>();

    public static URL modelDirURL() {
        return ResourceUtil.class.getResource("./model/");
    }

    public static URL dictionaryDirURL() {
        return ResourceUtil.class.getResource("./dictionary/");
    }

    private static File modelFileOf(String name) {
        File file = new File(modelDirURL().getPath(), name);
        Validate.isTrue(file.exists(), "Cannot find model: " + name + " in " + modelDirURL().getPath());
        return file;
    }

    private static File dictionaryFileOf(String name) {
        File file = new File(dictionaryDirURL().getPath(), name);
        Validate.isTrue(file.exists(), "Cannot find dictionary: " + name + " in " + dictionaryDirURL().getPath());
        return file;
    }


    public static <T extends BaseModel> T modelOf(String modelFileName, Class<T> modelClass) {
        Validate.notEmpty(modelFileName);
        Validate.notNull(modelClass);
        if (!modelCache.containsKey(modelFileName)) {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(ResourceUtil.modelFileOf(modelFileName));
                BaseModel model = modelClass.getConstructor(InputStream.class).newInstance(stream);
                modelCache.put(modelFileName, model);
            } catch (Exception e) {
                throw new NLPException(e);
            } finally {
                tryClose(stream);
            }
        }
        return (T) modelCache.get(modelFileName);
    }

    public static InputStream dictionaryInputStreamOf(String dictionaryFileName) {
        Validate.notEmpty(dictionaryFileName);
        try {
            return new FileInputStream(dictionaryFileOf(dictionaryFileName));
        } catch (FileNotFoundException e) {
            throw new NLPException(e);
        }
    }

    private static void tryClose(FileInputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
