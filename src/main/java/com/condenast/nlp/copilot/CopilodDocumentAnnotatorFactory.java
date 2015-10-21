package com.condenast.nlp.copilot;

import com.condenast.nlp.NLPException;
import com.condenast.nlp.copilot.annotators.DefaultCopilotDocumentAnnotator;
import com.condenast.search.corpus.utils.copilot.walker.CopilotDocument;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * Created by arau on 10/20/15.
 */
public class CopilodDocumentAnnotatorFactory {

    public static CopilotDocumentAnnotator buildFor(CopilotDocument copilotDocument) {
        Validate.notNull(copilotDocument);
        CopilotDocumentAnnotator annotator;
        String className = CopilodDocumentAnnotatorFactory.class.getPackage().getName() + ".annotators." +
                StringUtils.capitalize(copilotDocument.collectionName()) + "Annotator";
        try {
            annotator = (CopilotDocumentAnnotator) Class.forName(className).newInstance();
        } catch (ClassNotFoundException cnfe) {
            annotator = new DefaultCopilotDocumentAnnotator();
        } catch (Exception e) {
            throw new NLPException(e);
        }
        annotator.setCopilotDocument(copilotDocument);
        return annotator;
    }

}
