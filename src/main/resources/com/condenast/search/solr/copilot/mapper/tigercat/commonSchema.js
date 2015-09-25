// Common index fields needed by all entities on all brands.
commonSchema = {
    id: '.model > .id',
    createdAt_tdt: '.model > .createdAt',
    revisionCreatedAt_tdt: '.model > .revisionCreatedAt',
    revision_i: '.model > .revision',
    indexDate_tdt: {
        converter: {
            method: 'now'
        }
    },
    docType_s: '.model > .meta > .modelName',
    status_s: '.model > .__status',
    revStatus_s: '.model > .__revStatus',
    publishDate_tdt: '.model > .__publishDate',
    searchable_s: '.model > .__searchable',
    relId_ss: '.rels .id',
    archived_b: '.model > .meta > .archived',
    uri_s: '.model > .__uri'
};
