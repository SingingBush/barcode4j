Barcode4j
=========

![Java CI](https://github.com/SingingBush/barcode4j/workflows/Java%20CI/badge.svg)

Barcode4J is a flexible generator for barcodes written in Java and available under the Apache License v2.0. Features

### Forked from [Sourceforge barcode4j](https://sourceforge.net/p/barcode4j/):

Barcode4j was originally developed by Jeremias Märki and Marc Guillemot with code available on Sourceforge under Apache License v2.0.

It's not been updated in some time so this fork was made with the intention of merging in the various changes that people have tried to add over the years.

There is [another barcode4j on github](https://github.com/jeremycrosbie/barcode4j) but it's not being maintained and the svn history was not maintained.

This fork has the following goals:

- [x] Keeping the svn history
- [x] Enabling continuous integration for multiple Java versions
- [x] Dropping support for Java < 8 (1.7 will need to be minimum to support Java 12 and above)
- [x] Move to standard maven project structure
- [ ] Pulling in various svn patches and git merge requests
- [ ] Adding Test Coverage
- [ ] Publish build artifacts to maven central
- [ ] Support the latest Saxon version**

** I hope to add support for recent versions of Saxon (10 & 11) but now Saxonica ships 3 builds: Saxon-HE, Saxon-PE, and Saxon-EE. The first of which (Home Edition) is available via maven. I did the initial work to update but it turns out element extensibility is not available in Saxon Home Edition (the _net.sf.saxon.style.ExtensionElementFactory_ that is required is not available).


## Build

Maven (in future the project will only support maven)

```
mvn package
```

Ant builds can be performed using:

```
ant -buildfile barcode4j/build-dist.xml
```