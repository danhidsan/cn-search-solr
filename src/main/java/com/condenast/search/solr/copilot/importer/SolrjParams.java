package com.condenast.search.solr.copilot.importer;

public class SolrjParams {
    private final int batchSize;
    private final int commitEvery;
    private final boolean optimizeAtTheEnd;

    public static final SolrjParams FAST_INDEXING = new SolrjParams(100000, 500000, false);

    public SolrjParams() {
        this(1, 10, false);
    }

    public SolrjParams(int batchSize, int commitEvery, boolean optimizeAtTheEnd) {
        this.batchSize = batchSize > 0 ? batchSize : 1;
        this.commitEvery = commitEvery > 0 ? commitEvery : 10;
        this.optimizeAtTheEnd = optimizeAtTheEnd;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getCommitEvery() {
        return commitEvery;
    }

    public boolean shouldOptimizeAtTheEnd() {
        return optimizeAtTheEnd;
    }
}
