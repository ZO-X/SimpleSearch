# Simple Search


**Description:**
SimpleSearch is a full-text-search implementation which uses "inverted index" - an index where the keys are individual terms, and the associated values are sets of records that contain the term (with additional information about exact term position in this case)
- Java 8 application
- Maven project


**Build instruction:**
To build SimpleSearch.jar from the command line:
```
mvn package
```

**Usage:**
```
java -jar SimpleSearch.jar <path to indexable directory>
ex. java -jar SimpleSearch.jar C:\files
```
