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
        ensureCnOneEditIdxHasSomething();
    }

    private static void browseHomePage() {
        browseRequest("");
    }

    private static void browseRequest(String request) {
        lrf.qtype = "/browse";
        lrf.args.put(CommonParams.WT, "velocity");
        lrf.args.put("v.template", "browse");
        lrf.args.put("v.layout", "layout");
        try {
            html = h.query(lrf.makeRequest(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        lrf.qtype = null;
    }

    @Test
    public void testBrowseIsHtml() throws Exception {
        browseHomePage();
        assertIsHtmlResponse();
    }

    @Test
    public void testHasFacets() throws Exception {
        browseHomePage();
        assertHasFacetsFor("brandName_s");
        assertHasFacetsFor("docType_s");
        assertHasFacetsFor("tags_ss");
    }

    @Test
    public void testHasMLT() {
        browseHomePage();
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
