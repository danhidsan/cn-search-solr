#!/bin/sh
solr_config=${PWD}/target/test-classes/solr-config/
$SOLR_HOME/bin/solr start -f -s $solr_config -m 2g
