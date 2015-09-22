package com.condenast.search.solr;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class SolrConfigTestHelper {

    public static final String CN_ONE_EDIT = "cn-one-edit";

    public static URL solrConfigURL() {
        return SolrConfigTestHelper.class.getResource("/solr-config");
    }

    public static File cnOneConfigHome() {
        return new File(solrConfigURL().getPath(), "/" + CN_ONE_EDIT + "/conf/");
    }

    public static String cnOneEditSchemaXml() {
        return cnOneConfigHome().getAbsolutePath() + "/schema.xml";
    }

    public static String cnOneEditConfigXml() {
        return cnOneConfigHome().getAbsolutePath() + "/solrconfig.xml";
    }


    public static File testCopilotCorpus10DocsPerBrandPerCollectionRootDir() {
        return testCopilotCorpusRootDir("tenDocPerBrandPerCollection");
    }

    public static File testCopilotCorpusRootDir(String dirName) {
        URL dir = SolrConfigTestHelper.class.getResource("/");
        File rootDir = new File(dir.getPath() + "/copilotCorpus/" + dirName + "/prod");
        assertTrue(rootDir.exists());
        return rootDir;
    }

}