/*
 * Converter functions referenced in search schema configs
 * to massage or generate index data.
 */

var converters = {};

converters._getConverter = function (method) {
    if (!_.isFunction(converters[method])) {
        throw new TypeError('ERROR: SearchDoc value converter [' + method + '] is not defined.');
    }
    return converters[method];
};

//executes single converter or array of converters on matchvalue.
converters.convert = function (val, converter) {
    if (_.isUndefined(converter)) {
        return val;
    }

    // Retain the original value so that in the event of a bad converter
    // or misconfig the whole document doesn't get booted out the index.
    // If an error is thrown we can simply return the original value.
    var resVal = _.clone(val);

    var convertersMethods = _.isArray(converter) ? converter : [converter];
    var success = convertersMethods.every(function (c) {
        try {
            var cFunc = converters._getConverter(c.method);
            var params = _.isEmpty(resVal) ? [] : [resVal];
            params = _.isUndefined(c.args) ? params : params.concat(c.args);
            resVal = cFunc.apply(null, params);
            return true;
        } catch (e) {
            return false;
        }
    });

    return success ? resVal : val;
};

/*
 * Returns an array of keys that correspond to boolean `true` values.
 *
 * features: {
 *  urban: true,
 *  pool: true,
 *  daycare: false
 * }
 *
 * returns => ['urban', 'pool']
 *
 * @param {object} or [{object}, {object}....]
 * @returns array of key values.
 */
converters.mapToKey = function (v) {
    var vals = _.isArray(v) ? v : [v];
    var isTrue = _.partial(_.isEqual, true);

    return _.flatten(vals.map(function (val) {
        // return keys for values that `=== true`
        return Object.keys(_.pick(val, isTrue));
    }));
};

converters.lowercase = function (v) {
    return _.isArray(v) ? _.invoke(v, 'toLowerCase') : v.toLowerCase();
};

converters.uppercase = function (v) {
    return _.isArray(v) ? _.invoke(v, 'toUpperCase') : v.toUpperCase();
};

/*
 * Returns the first argument.
 *
 * @param {string}
 * @returns string value.
 */
converters.constant = _.identity;

converters.now = function () {
    return new Date().toISOString();
};

/*
 * Formats a single date string value or array of date string values as
 * a string using the ISO-8601 standard using the format ('YYYY-MM-DD[T]HH:mm:ss.SSS[Z]).
 *
 * @param {String | Array} - input date strings. Assumed to be in UTC unless timezone offsets provided.
 * @return {String | Array } - returns formatted date string value in ISO standard format.
 *
 */
converters.toISOString = function (v) {
    if (!_.isString(v) && !_.isArray(v)) {
        return v;
    }

    var dateVals = _.isArray(v) ? v : [v];

    var isDateValid = dateVals.every(function (d) {
        return moment(d).isValid();
    });

    if (isDateValid) {
        var dateFormatVal = 'YYYY-MM-DD[T]HH:mm:ss.SSS[Z]';
        var results = _.map(dateVals, function (dte) {
            return moment.utc(dte).format(dateFormatVal);
        });
        return results.length === 1 ? _.first(results) : results;
    }

    return v;
};
