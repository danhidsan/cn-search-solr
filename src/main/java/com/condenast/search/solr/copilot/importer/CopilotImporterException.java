package com.condenast.search.solr.copilot.importer;

/**
 * Created by arau on 9/14/15.
 */
public class CopilotImporterException extends RuntimeException {

    public CopilotImporterException(Exception e) {
        super(e);
    }

    public CopilotImporterException(String msg, Exception e) {
        super(msg, e);
    }

}
