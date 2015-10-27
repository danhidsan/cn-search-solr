package com.condenast.nlp.opennlp;

import com.condenast.nlp.NLPException;
import opennlp.tools.util.model.BaseModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arau on 10/15/15.
 */
public class ResourceUtil {

    protected static transient Logger log = LoggerFactory.getLogger(ResourceUtil.class);

    private final static Map<String, BaseModel> modelCache = new HashMap<>();
    public static final String EN_TOKEN_MODEL_BIN = "en-token.bin";

    public static URL modelDirURL() {
        return ResourceUtil.class.getResource("./model/");
    }

    public static Path bratDirPath() {
        return new File(ResourceUtil.class.getResource("./brat/").getPath()).toPath();
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

    public static void copyBratConfigFilesIfNotExist(File baseDirFile) {
        try {
            Files.newDirectoryStream(bratDirPath()).forEach(p -> {
                File destVisualConf = new File(baseDirFile, p.getFileName().toString());
                if (!destVisualConf.exists()) {
                    try {
                        FileUtils.copyFile(p.toFile(), destVisualConf);
                    } catch (IOException e) {
                        log.warn("Cannot copy conf file from: " + p.toAbsolutePath() + " to: " + destVisualConf.getAbsolutePath(), e);
                    }
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
