package com.condenast.search.solr.copilot.indexer;

import com.condenast.search.corpus.utils.copilot.visitor.AbstractVisitor;
import com.condenast.search.corpus.utils.copilot.walker.BrandWalker;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import com.condenast.search.corpus.utils.copilot.walker.CorporaWalker;
import com.condenast.search.solr.copilot.mapper.DocMapper;
import com.condenast.search.solr.copilot.mapper.DocMapperBase;
import org.apache.commons.lang.Validate;
import org.apache.solr.common.SolrInputDocument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arau on 9/8/15.
 */
public class Importer extends AbstractVisitor {

    private List<ImporterListener> listeners = new ArrayList<>();

    private CorporaWalker walker;
    private SolrInputDocument currentSolrInputDocument;
    private CopilotDocument currentCopilotDocument;

    private DocMapper docMapperEntryPoint;
    private CopilotDocument currentSearchSchema;

    public static IDocMappers withCorporaWalker(CorporaWalker walker) {
        return new Builder(walker);
    }

    public void run() {
        walker.run(this);
    }

    public interface IDocMappers {
        IListeners andDocMapperEntryPoint(DocMapper docMapper);

        IListeners andDocMappers(DocMapper... docMappers);
    }

    public interface IListeners {
        IBuild andListeners(ImporterListener... listeners);
    }

    public interface IBuild {
        IBuild andMaxDocs(int maxDocs);

        Importer build();
    }

    public static class Builder implements IListeners, IDocMappers, IBuild {

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

        @Override
        public IListeners andDocMapperEntryPoint(DocMapper docMapper) {
            Validate.notNull(docMapper);
            importer.docMapperEntryPoint = docMapper;
            return this;
        }

        @Override
        public IListeners andDocMappers(DocMapper... docMappers) {
            Validate.notEmpty(docMappers);
            DocMapper entryPoint = docMappers[0];
            if (docMappers.length > 1) entryPoint = DocMapperBase.assembleChain(docMappers);
            return this.andDocMapperEntryPoint(entryPoint);
        }
    }

    private Importer() {
        super(Integer.MAX_VALUE);
    }

    @Override
    protected void processDocument(CopilotDocument copilotDocument) {
        currentCopilotDocument = copilotDocument;
        currentSolrInputDocument = new SolrInputDocument();
        docMapperEntryPoint.map(currentCopilotDocument, currentSearchSchema, currentSolrInputDocument);
        listeners.forEach(l -> l.onDocument(currentCopilotDocument, currentSolrInputDocument));
        currentCopilotDocument = null;
        currentSolrInputDocument = null;
    }

    @Override
    protected void processDocumentSearchSchema(CopilotDocument searchSchemaDocument) {
        this.currentSearchSchema = searchSchemaDocument;
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
        return walker == null;
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
        this.walker = corporaWalker;
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
