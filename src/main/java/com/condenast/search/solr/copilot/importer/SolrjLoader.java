package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.Validate;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by arau on 9/14/15.
 */
public class SolrjLoader implements ImporterListener {

    private int counter = 0;

    private Collection<SolrInputDocument> batch = new ArrayList<>();

    protected static org.slf4j.Logger logger = LoggerFactory.getLogger(SolrjLoader.class.getName());

    private final SolrClient solrClient;

    private final SolrjParams params;

    public SolrjLoader(final SolrClient solrClient) {
        this(solrClient, new SolrjParams());
    }

    public SolrjLoader(final SolrClient solrClient, SolrjParams solrjParams) {
        Validate.notNull(solrClient, "solrClient cannot be null");
        Validate.notNull(solrjParams, "solrjParams cannot be null");
        this.solrClient = solrClient;
        this.params = solrjParams;
    }

    @Override
    public void onDocument(CopilotDocument copilotDocument, SolrInputDocument solrInputDocument) {
        batch.add(solrInputDocument);
        counter++;
        if (batchIsComplete() || shouldCommit()) {
            deliverBatch();
        }
        if (shouldCommit()) {
            doServerCommit();
        }
    }

    private boolean shouldCommit() {
        return counter % params.getCommitEvery() == 0;
    }

    private void doServerCommit() {
        try {
            logger.info("Started SOLR COMMIT with commitEvery=" + params.getCommitEvery() + " and totProcDocs=" +
                    counter + "...");
            solrClient.commit();
            logger.info("...COMMIT done.");
        } catch (Exception e) {
            throw new CopilotImporterException("Exception during SOLR COMMIT: " + e.getMessage(), e);
        }
    }


    private void deliverBatch() {
        try {
            logger.info("Started delivering BATCH of solrInputDocs with " + batch.size() + " docs...");
            solrClient.add(batch);
            logger.info("...delivering BATCH of solrInputDocs done.");
        } catch (Exception e) {
            throw new CopilotImporterException("Exception while delivering copilot solrInputDoc batch of size: " + batch.size(), e);
        }
        batch.clear();
    }


    private boolean batchIsComplete() {
        return counter % params.getBatchSize() == 0;
    }

    @Override
    public void onError(Exception e, CopilotDocument currentCopilotDocument, SolrInputDocument currentSolrInputDocument) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void onStart(Importer importer) {

    }

    @Override
    public void onEnd(Importer importer) {
        if (batch.size() > 0) {
            deliverBatch();
            doServerCommit();
        }
        if (params.shouldOptimizeAtTheEnd()) {
            doOptimize();
        }
    }

    private void doOptimize() {
        try {
            solrClient.optimize();
        } catch (Exception e) {
            throw new CopilotImporterException("Exception while optimizing", e);
        }
    }
}
