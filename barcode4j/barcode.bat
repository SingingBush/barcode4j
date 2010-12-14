@ECHO OFF

REM $Id: barcode.bat,v 1.6 2010-12-14 14:16:41 jmaerki Exp $

if "%JAVA_HOME%" == "" goto noJavaHome

set TMP_BARCODE4J_HOME=
rem %~dp0 is the expanded pathname of the current script under NT
if "%OS%"=="Windows_NT" set TMP_BARCODE4J_HOME=%~dp0

set TMP_LIBDIR=%TMP_BARCODE4J_HOME%lib
if exist "%TMP_BARCODE4J_HOME%build\barcode4j.jar" goto srcdist
:bindist
set TMP_CLASSPATH=%TMP_BARCODE4J_HOME%barcode4j.jar
goto skip1
:srcdist
set TMP_CLASSPATH=%TMP_BARCODE4J_HOME%build\barcode4j.jar
:skip1
set TMP_CLASSPATH=%TMP_CLASSPATH%;%TMP_LIBDIR%\avalon-framework-4.2.0.jar
set TMP_CLASSPATH=%TMP_CLASSPATH%;%TMP_LIBDIR%\commons-cli-1.0.jar
REM ECHO %TMP_CLASSPATH%

if "%OS%"=="Windows_NT" goto WinNT

:Win98
"%JAVA_HOME%\bin\java" -cp "%TMP_CLASSPATH%" org.krysalis.barcode4j.cli.Main %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO exit

:WinNT
"%JAVA_HOME%\bin\java" -cp "%TMP_CLASSPATH%" org.krysalis.barcode4j.cli.Main %*
GOTO exit


:noJavaHome
ECHO Please set the JAVA_HOME environment variable.

:exit