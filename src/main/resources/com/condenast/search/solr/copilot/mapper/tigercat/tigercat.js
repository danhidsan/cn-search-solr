function toSolrDoc(copilotDocJson, searchSchemaJson) {

    var copilotDocObj = JSON.parse(copilotDocJson);
    var searchSchemaObj = JSON.parse(searchSchemaJson);

    var schema = _.defaults(_.clone(searchSchemaObj, true), commonSchema);

    var solrDoc = {};

    for (var key in schema) {
        if (!schema.hasOwnProperty(key)) continue;
        var val = schema[key];
        if (_.isFunction(val)) {
            //a function which derives a solr doc value
            //from the Tsugu model/relModel objects
            var searchFieldFunc = val;
            searchFieldFunc(model, rels, function (err, result) {
                if (err) {
                    throw err;
                }
                solrDoc[key] = result;
            });
        } else {
            var selector = extractSelector(val);
            var matchVal = match(selector, copilotDocObj);
            var converted = converters.convert(matchVal, val.converter);

            // if no value, check if a default is specified.
            if (_.isEmpty(converted) && (val.default && !_.isEmpty(val.default))) {
                converted = val.default;
            }

            //assign value to solr doc
            if (!_.isEmpty(converted)) {
                solrDoc[key] = converted;
            }

        }
    }

    return JSON.stringify(solrDoc);
}


function extractSelector(val) {
    //extract selector
    if (_.isString(val)) {
        return val;
    } else if (_.isObject(val) && val.selector) {
        return val.selector;
    }
    return '';
}


function match(sel, obj) {
    if (!sel) {
        return [];
    }
    var comp = JSONSelect.compile(sel);
    var match = comp.match(obj);
    return _.uniq(match.sort(), true);
}
