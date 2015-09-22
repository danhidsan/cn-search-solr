CN Search and SOLR
============

Maven project for SOLR custom configuration and code TDD and CI. Contains:

- JUnit unit and integration tests of custom configuration and code.

- Importer package to import a downloaded Copilot Corpus from the FS (using the [cn-search-corpus-utils]
(https://github.com/CondeNast/cn-search-corpus-utils)
project) into SOLR. The Importer package use the Mapper package to map a json FS document into a Solr Document (see
below)

- Mapper package mapping a copilot json document to a Solr Document using
 searchSchema.json and [json select] (http://jsonselect.org) mimicking what [Tigercat] (https://github.com/CondeNast/tigercat) does.

- Configurations under test are in [src/test/resources/solr-config] (src/test/resources/solr-config). Currently there is only the copilot unified (cross brands) edit index called cn-one-edit.

## Requirements

You need the following:

- Java (v1.8+)
- Maven (v3+)

## Testing unified in

To run the tests:

````
$ mvn verify
````




