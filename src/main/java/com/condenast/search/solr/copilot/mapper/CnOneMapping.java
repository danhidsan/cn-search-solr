package com.condenast.search.solr.copilot.mapper;

import com.condenast.search.solr.copilot.mapper.tigercat.TigercatMapping;

/**
 * Created by arau on 10/13/15.
 */
public class CnOneMapping {

    public static DocMapper build() {
        return DocMapperBase.assembleChain(new TigercatMapping(), new CommonMapping());
    }

}
