Barcode4j
=========

[![Maven](https://github.com/SingingBush/barcode4j/actions/workflows/maven.yml/badge.svg)](https://github.com/SingingBush/barcode4j/actions/workflows/maven.yml)
[![Ant](https://github.com/SingingBush/barcode4j/actions/workflows/ant.yml/badge.svg)](https://github.com/SingingBush/barcode4j/actions/workflows/ant.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.singingbush/barcode4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.singingbush/barcode4j)
[![Javadocs](https://www.javadoc.io/badge/com.singingbush/barcode4j.svg)](https://www.javadoc.io/doc/com.singingbush/barcode4j)
[![CodeQL](https://github.com/SingingBush/barcode4j/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/SingingBush/barcode4j/actions/workflows/github-code-scanning/codeql)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=barcode4j&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=barcode4j)

Barcode4J is a flexible generator for barcodes written in Java and available under the Apache License v2.0. Features

### Forked from [Sourceforge barcode4j](https://sourceforge.net/p/barcode4j/):

Barcode4j was originally developed by Jeremias Märki and Marc Guillemot with code available on Sourceforge under Apache License v2.0.

It's not been updated in some time so this fork was made with the intention of merging in the various changes that people have tried to add over the years.

There is [another barcode4j on github](https://github.com/jeremycrosbie/barcode4j) but it's not being maintained and the svn history was not maintained.

This fork has the following goals:

 - [x] Keeping the svn history
 - [x] Enabling continuous integration for multiple Java versions
 - [x] Support latest JDK versions (for now Java 1.7 and above are supported but future releases will require JDK 11 or above)
 - [x] Support GraalVM Native Image (changes were needed to allow barcode4j to be AOT compiled for use with [GraalVM](https://www.graalvm.org/) and/or [Quarkus](https://quarkus.io/))
 - [x] Move to standard maven project structure (Ant build will be removed later)
 - [x] Publish build artifacts to maven central [singingbush group on maven.org](https://search.maven.org/search?q=com.singingbush)
 - [x] Improve Test Coverage (this is ongoing but better than it was)
 - [x] Publish JaCoCo report results
 - [x] Add static analysis ([SonarCloud](https://sonarcloud.io/summary/overall?id=barcode4j), CodeQL, and Qodana)
 - [ ] Add JPMS support (**in version 3 and above**, 2.* releases will continue to support JDK 7 for now)
 - [ ] Pulling in various svn patches and git merge requests
 - [ ] Either remove or update the Saxon code to support the latest Saxon EE version**
 - [ ] Remove dependency on Avalon-Framework (**in version 3 and above**, Avalon was retired years ago. See: #15)

** Support for Saxon was never published to Maven central before and these days Saxonica ship 3 versions (Saxon-HE, Saxon-PE, and Saxon-EE). The first of which (Home Edition) is available via maven central but the _net.sf.saxon.style.ExtensionElementFactory_ class that is required is not available. Saxon EE is now available via Saxonica's maven repository so it may be able to revive the code to use Saxon 11 or 12. If this proves to be a pain then the related code will be removed.

## Compatibility with the original barcode4j

### V2 (drop in replacement):

This fork should work as a drop in replacement of `net.sf.barcode4j:barcode4j:2.1` for most users. Please note however that the original was a fat-jar that included classes that are now in the `barcode4j-ant`, `barcode4j-cli`, `barcode4j-servlet`, and `barcode4j-xalan` sub-modules. The singingbush barcode4j is more aligned to the [barcode4j-light](https://search.maven.org/search?q=a:barcode4j-light) package that was last released in 2008.

That said, if you've been using `net.sf.barcode4j:barcode4j:2.1` then switching to the latest `com.singingbush:barcode4j:2.+` should simply be a case of choosing the dependencies you require. In most cases this is likely to be a combination of `com.singingbush:barcode4j:2.+` and `com.singingbush:barcode4j-fop-ext:2.+`.

Initially the project will continue to be a compatible drop-in replacement for existing use of `net.sf.barcode4j:barcode4j:2.1`:

 - The v2.* releases will continue to have the existing _org.krysalis.barcode4j_ package names.
 - The _barcode4j_, _barcode4j-fop-ext_, _barcode4j-xgc_ and _barcode4j-xalan_ artifacts are published to maven central.
 - Releases will support JDK 1.7 and above.

```xml
    <dependency>
        <groupId>com.singingbush</groupId>
        <artifactId>barcode4j</artifactId>
        <version>2.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.singingbush</groupId>
        <artifactId>barcode4j-xalan</artifactId>
        <version>2.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.singingbush</groupId>
        <artifactId>barcode4j-xgc</artifactId>
        <version>2.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.singingbush</groupId>
        <artifactId>barcode4j-fop-ext</artifactId>
        <version>2.2.3</version>
    </dependency>
```

### V3 (minor changes):

In version 3 the project will continue to be compatible for the most part. Older JDK's won't be supported but most users will not be affected.

The only changes required by users will be to change import paths to the newer package name:

 - Package names will be updated to _com.singingbush.barcode4j_ equivalent
 - Drop support for JDK 7 (potentially only support JDK 11 and above)
 - Drop all uses of the now defunct Avalon Framework. See [issue #15](https://github.com/SingingBush/barcode4j/issues/15)
 - Saxon support will be updated to work with the latest Saxon EE ([Saxonica](https://www.saxonica.com/) now have a Maven repository for Saxon EE)
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
