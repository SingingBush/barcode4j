Barcode4j
=========

[![Maven](https://github.com/SingingBush/barcode4j/actions/workflows/maven.yml/badge.svg)](https://github.com/SingingBush/barcode4j/actions/workflows/maven.yml)
[![Ant](https://github.com/SingingBush/barcode4j/actions/workflows/ant.yml/badge.svg)](https://github.com/SingingBush/barcode4j/actions/workflows/ant.yml)

Barcode4J is a flexible generator for barcodes written in Java and available under the Apache License v2.0. Features

### Forked from [Sourceforge barcode4j](https://sourceforge.net/p/barcode4j/):

Barcode4j was originally developed by Jeremias Märki and Marc Guillemot with code available on Sourceforge under Apache License v2.0.

It's not been updated in some time so this fork was made with the intention of merging in the various changes that people have tried to add over the years.

There is [another barcode4j on github](https://github.com/jeremycrosbie/barcode4j) but it's not being maintained and the svn history was not maintained.

This fork has the following goals:

 - [x] Keeping the svn history
 - [x] Enabling continuous integration for multiple Java versions
 - [x] Dropping support for Java < 8 (supporting newer LTS releases such as 11 and 17 is priority)
 - [x] Move to standard maven project structure
 - [ ] Pulling in various svn patches and git merge requests
 - [ ] Adding Test Coverage
 - [ ] Publish build artifacts to maven central
 - [ ] Support the latest Saxon version**
 - [ ] Add JPMS support (**in version 3 and above**, 2.* releases will continue to support JDK 8)

** I hope to add support for recent versions of Saxon (10 & 11) but now Saxonica ships 3 builds: Saxon-HE, Saxon-PE, and Saxon-EE. The first of which (Home Edition) is available via maven. I did the initial work to update but it turns out element extensibility is not available in Saxon Home Edition (the _net.sf.saxon.style.ExtensionElementFactory_ that is required is not available).

## Compatibility with the original barcode4j

### V2 (drop in replacement):

Initially the project will continue to be a compatible drop-in replacement for existing use of `net.sf.barcode4j:barcode4j:2.1`:

 - The v2.* releases will continue to have the existing _org.krysalis.barcode4j_ package names.
 - Both the _barcode4j_ and _barcode4j-fop-ext_ artifacts will be published.
 - Releases will support JDK 1.7 and above.

### V3 (minor changes):

In version 3 the project will continue to be compatible for the most part. Older JDK's won't be supported but most users will not be affected.

The only changes required by users will be to change import paths to the newer package name:

 - Package names will be updated to _com.singingbush.barcode4j_ equivalent
 - Drop support for JDK 7 (potentially only support JDK 11 and above)
 - Drop all uses of the now defunct Avalon Framework. See [issue #15](https://github.com/SingingBush/barcode4j/issues/15)
 - Remove Saxon support (as [Saxonica](https://www.saxonica.com/) don't include the required interface in Saxon HE, and it seems the saxon extension was never published)
 - Remove the Ant build
 - Support Java modules (JPMS) via proper use of _module-info.java_ files in the source. (If JDK 8 is supported this will be via multi-release jar)

## Build

### Maven (in future the project will only support maven)

```
mvn package
```

### Ant

Make sure to have both ant and ant-junit5 installed. On Fedora this can be done with `sudo dnf install ant ant-junit5`, on Ubuntu using `sudo apt install ant ant-optional junit5` should be enough but Ubuntu doesn't ship _ant-junitlauncher_ yet, see [launchpad bug](https://bugs.launchpad.net/ubuntu/+source/ant/+bug/1998045).

Builds can be performed using:

```
ant -buildfile barcode4j/build.xml
```

or

```
ant -buildfile barcode4j/build-dist.xml
```
