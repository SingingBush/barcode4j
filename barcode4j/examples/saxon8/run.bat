@ECHO OFF
SET LOCAL_CLASSPATH=..\..\build\barcode4j.jar
SET LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;..\..\lib\avalon-framework-4.1.5.jar
SET LOCAL_CLASSPATH=%LOCAL_CLASSPATH%;..\..\lib\saxon8.jar

%JAVA_HOME%\bin\java -cp "%LOCAL_CLASSPATH%" net.sf.saxon.Transform barcode.xml example-saxon8.xslt
