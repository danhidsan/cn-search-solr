package com.condenast.search.solr.copilot.importer;

import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.BrandWalker;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.CorporaWalker;
import com.condenast.search.solr.copilot.mapper.CommonMapping;
import com.condenast.search.solr.copilot.mapper.DocMapper;
import com.condenast.search.solr.copilot.mapper.SearchSchemaMapping;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arau on 9/8/15.
 */
public class Importer extends AbstractVisitor {

    private SearchSchemaMapping searchSchemaMapping;

    private List<ImporterListener> listeners = new ArrayList<>();

    private CorporaWalker walker;
    private SolrInputDocument currentSolrInputDocument;
    private CopilotDocument currentCopilotDocument;

    private CorporaWalker corporaWalker;

    private BrandWalker brandWalker;

    public static IListeners withCorporaWalker(CorporaWalker walker) {
        return new Builder(walker);
    }

    public void run() {
        walker.run(this);
    }

    public interface IListeners {
        IBuild andListeners(ImporterListener... listeners);
    }

    public interface IBuild {
        IBuild andMaxDocs(int maxDocs);

        Importer build();
    }

    public static class Builder implements IListeners, IBuild {

        private Importer importer = new Importer();

        private Builder(CorporaWalker walker) {
            Validate.notNull(walker, "walker cannot be null");
            importer.walker = walker;
        }

        public Builder andListeners(ImporterListener... listeners) {
            Validate.notEmpty(listeners, "listeners cannot be empty list");
            importer.listeners = Arrays.asList(listeners);
            return this;
        }

        public Builder andMaxDocs(int maxDocs) {
            importer.maxDocs = maxDocs;
            return this;
        }

        public Importer build() {
            return importer;
        }
    }

    private Importer() {
        super(Integer.MAX_VALUE);
    }

    @Override
    protected void processDocument(CopilotDocument copilotDocument) {
        currentCopilotDocument = copilotDocument;
        currentSolrInputDocument = DocMapper.map(currentCopilotDocument, searchSchemaMapping, CommonMapping.INSTANCE);
        for (ImporterListener listener : listeners) {
            listener.onDocument(currentCopilotDocument, currentSolrInputDocument);
        }
        currentCopilotDocument = null;
        currentSolrInputDocument = null;
    }

    @Override
    protected void processDocumentSearchSchema(CopilotDocument searchSchemaDocument) {
        this.searchSchemaMapping = new SearchSchemaMapping(searchSchemaDocument.toJson());
    }

    @Override
    public void onError(Exception e) {
        super.onError(e);
        for (ImporterListener listener : listeners) {
            listener.onError(e, currentCopilotDocument, currentSolrInputDocument);
        }
    }

    @Override
    public void onBrandStart(BrandWalker walker) {
        super.onBrandStart(walker);
        this.brandWalker = walker;
        if (brandWalking()) {
            notifyStart();
        }
    }

    private void notifyStart() {
        for (ImporterListener listener : listeners) {
            listener.onStart(this);
        }
    }

    private boolean brandWalking() {
        return corporaWalker == null;
    }

    @Override
    public void onBrandEnd(BrandWalker walker) {
        super.onBrandEnd(walker);
        if (brandWalking()) {
            notifyEnd();
        }
    }

    @Override
    public void onCorporaStart(CorporaWalker corporaWalker) {
        super.onCorporaStart(corporaWalker);
        this.corporaWalker = corporaWalker;
        notifyStart();
    }

    @Override
    public void onCorporaEnd(CorporaWalker corporaWalker) {
        super.onCorporaEnd(corporaWalker);
        notifyEnd();
    }

    private void notifyEnd() {
        for (ImporterListener listener : listeners) {
            listener.onEnd(this);
        }
    }
}
