/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.condenast.search.solr;

import org.apache.solr.request.SolrQueryRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is an example of how to write a JUnit tests for Solr using the
 * SolrTestCaseJ4
 */

public class SampleIT extends AbstractIT {

    @BeforeClass
    public static void beforeClass() throws Exception {
        initCnOneEditIdx();
    }


    @Before
    public void before() {
        xmlResponse();
    }

    /**
     * Demonstration of some of the simple ways to use the base class
     */
    @Test
    public void testSimple() {
        assertU("Simple assertion that adding a document works", adoc("id", "4055", "title", "Hoss the Hoss man " + "Hostetter"));

    /* alternate syntax, no label */
        assertU(adoc("id", "4056", "title", "Some Other Guy"));

        assertU(commit());
        assertU(optimize());

        assertQ("couldn't find title hoss", req("title:Hoss"), "//result[@numFound=1]", "//str[@name='id'][.='4055']");
    }

    /**
     * Demonstration of some of the more complex ways to use the base class
     */
    @Test
    public void testAdvanced() throws Exception {
        assertU("less common case, a complex addition with options", add(doc("id", "4059", "title", "Who Me?"), "overwrite", "false"));

        assertU("or just make the raw XML yourself", "<add overwrite=\"false\">" +
                doc("id", "4059", "title", "Who Me Again?") + "</add>");

    /* or really make the xml yourself */
        assertU("<add><doc><field name=\"id\">4055</field>" + "<field name=\"title\">Hoss the Hoss man " +
                "Hostetter</field>" + "</doc></add>");

        assertU("<commit/>");
        assertU("<optimize/>");
        
    /* access the default LocalRequestFactory directly to make a request */
        SolrQueryRequest req = lrf.makeRequest("title:Hoss");
        assertQ("couldn't find title hoss", req, "//result[@numFound=1]", "//str[@name='id'][.='4055']");

    }

    @Test
    public void testSimpleJSON() throws Exception {

        assertU(adoc("id", "4732", "title", "Some Other Guy"));

        assertU(commit());
        assertU(optimize());


        assertQJNumFound("id:4732", 1);

    }

}


