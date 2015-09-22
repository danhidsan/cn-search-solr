package com.condenast.search.solr.copilot.config.cnOneEdit;

import com.condenast.search.solr.AbstractIT;
import org.apache.solr.common.params.CommonParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by arau on 9/18/15.
 */
public class BrowseCnOneEditIT extends AbstractIT {

    private static String html;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCnOneEditIdx();
        ensureIndexHasSomething();
        browseHomePage();
    }

    private static void browseHomePage() throws Exception {
        lrf.qtype = "/browse";
        lrf.args.put(CommonParams.WT, "velocity");
        lrf.args.put("v.template", "browse");
        lrf.args.put("v.layout", "layout");
        html = h.query(lrf.makeRequest(""));
    }

    @AfterClass
    public static void afterClass() throws Exception {
        lrf.qtype = null;
    }

    @Test
    public void testBrowseIsHtml() throws Exception {
        assertIsHtmlResponse();
    }

    @Test
    public void testHasFacets() throws Exception {
        assertHasFacetsFor("brandName_s");
        assertHasFacetsFor("collectionName_s");
        assertHasFacetsFor("tags_ss");
    }

    @Test
    public void testHasMLT() throws Exception {
        assertHasMLT();
    }

    private void assertHasMLT() {
        assertTrue("/browse should contain mlt link", html.contains("<span class=\"mlt\">"));
        assertTrue("/browse should contain mlt link", html.contains("More Like This</a>"));
    }

    private void assertIsHtmlResponse() {
        assertTrue("/browse should return html", html.contains("<html>"));
    }

    private void assertHasFacetsFor(String fieldName) {
        assertTrue("/browse should show facet for " + fieldName, html.contains("<span " +
                "class=\"facet-field\">" + fieldName + "</span>"));
    }

}
