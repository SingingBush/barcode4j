/*
 * Copyright 2002-2004 Jeremias Maerki.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krysalis.barcode4j.cli;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the command line application
 * @author Jeremias Maerki
 * @version $Id: CommandLineTestCase.java,v 1.3 2004-10-02 14:58:23 jmaerki Exp $
 */
public class CommandLineTestCase {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private ExitHandlerForTests exitHandler;

//    private void dumpResults() throws Exception {
//        System.out.println("Msg: " + this.exitHandler.getLastMsg());
//        System.out.println("Exit code: " + this.exitHandler.getLastExitCode());
//
//        final Throwable lastThrowable = this.exitHandler.getLastThrowable();
//        if (lastThrowable != null) {
//            lastThrowable.printStackTrace(System.out);
//            //System.out.println(org.apache.avalon.framework.ExceptionUtil.printStackTrace(lastThrowable));
//        }
//        System.out.println("--- stdout (" + this.out.size() + ") ---");
//        System.out.println(new String(this.out.toByteArray(), "US-ASCII"));
//        System.out.println("--- stderr (" + this.err.size() + ") ---");
//        System.out.println(new String(this.err.toByteArray(), "US-ASCII"));
//        System.out.println("---");
//    }

    private void callCLI(String[] args) {
        Main app = new Main();
        try {
            app.handleCommandLine(args);
        } catch (SimulateVMExitError se) {
            //ignore
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        this.out = new ByteArrayOutputStream();
        this.err = new ByteArrayOutputStream();
        Main.stdout = new PrintStream(this.out);
        Main.stderr = new PrintStream(this.err);
        this.exitHandler = new ExitHandlerForTests();
        Main.setExitHandler(this.exitHandler);
    }

    @Test
    void testSVG() throws Exception {
        final String[] args = {"-s", "ean13", "9771422985503+00006"};
        callCLI(args);
        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "No output");
        assertTrue(this.err.size() == 0, "No output on stderr expected");
    }

    @Test
    void testEPS() throws Exception {
        final String[] args = {"-s", "ean13", "-f", "eps", "9771422985503+00006"};
        callCLI(args);
        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "No output");
        assertTrue(this.err.size() == 0, "No output on stderr expected");
    }

    @Test
    void testBitmapJPEG() throws Exception {
        final String[] args = {"-s", "ean13", "-f", "image/jpeg", "9771422985503+00006"};
        callCLI(args);
        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "No output");
        assertTrue(this.err.size() == 0, "No output on stderr expected");
    }

    @Test
    void testNoArgs() throws Exception {
        final String[] args = new String[0];
        callCLI(args);
        assertEquals(-2, this.exitHandler.getLastExitCode(), "Exit code must be -2");
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "CLI help expected on stdout");
        assertTrue(this.err.size() > 0, "Error message expected on stderr");
    }

    @Test
    void testUnknownArg() throws Exception {
        final String[] args = {"--badArgument"};
        callCLI(args);
        assertEquals(-2, this.exitHandler.getLastExitCode(), "Exit code must be -2");
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "CLI help expected on stdout");
        assertTrue(this.err.size() > 0, "Error message expected on stderr");
    }

    @Test
    void testWrongConfigFile() throws Exception {
        final String[] args = {"-c", "NonExistingConfigFile", "9771422985503+00006"};
        callCLI(args);
        assertEquals(-3, this.exitHandler.getLastExitCode(), "Exit code must be -3");
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() == 0,
                "In case of error stdout may only be written to if there's a problem with the command-line");
        assertTrue(this.err.size() > 0, "Error message expected on stderr");
    }

    @Test
    void testValidConfigFile() throws Exception {
        final File cfgFile = loadTestFile("xml/good-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(), "9771422985503+00006"};
        callCLI(args);
        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
    }

    @Test
    void testBadConfigFile() throws Exception {
        final File cfgFile = loadTestFile("xml/bad-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(), "9771422985503+00006"};
        callCLI(args);

        assertEquals(-6, this.exitHandler.getLastExitCode(), "Exit code must be -6");
        assertNotNull(this.exitHandler.getLastMsg());
        assertNotNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() == 0, "In case of error stdout may only be written to if there's a problem with the command-line");
        assertTrue(this.err.size() > 0, "Error message expected on stderr");
    }

    @Test
    void testToFile() throws Exception {
        File out = File.createTempFile("krba", ".tmp");
        final String[] args = {"-s", "ean-13", "-o", out.getAbsolutePath(), "9771422985503+00006"};
        callCLI(args);

        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue(this.out.size() > 0, "Application header expected on stdout");
        assertTrue(this.err.size() == 0, "No output expected on stderr");
        assertTrue(out.exists(), "Target file does not exist");
        assertTrue(out.length() > 0, "Target file must not be empty");
        if (!out.delete()) {
            fail("Target file could not be deleted. Not closed?");
        }
    }

    @Test
    void testDPI() throws Exception {
        File out100 = File.createTempFile("krba", ".tmp");
        final String[] args100 = {"-s", "ean-13",
                 "-o", out100.getAbsolutePath(),
                 "-f", "jpeg",
                 "-d", "100", "9771422985503+00006"};
        callCLI(args100);

        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertTrue(out100.exists(), "Target file does not exist");

        File out300 = File.createTempFile("krba", ".tmp");
        final String[] args300 = {"-s", "ean-13",
                 "-o", out300.getAbsolutePath(),
                 "-f", "jpeg",
                 "--dpi", "300", "9771422985503+00006"};
        callCLI(args300);
        assertEquals(0, this.exitHandler.getLastExitCode(), "Exit code must be 0");
        assertTrue(out300.exists(), "Target file does not exist");

        assertTrue(out300.length() > out100.length(), "300dpi file must be greater than the 100dpi file");
        if (!out100.delete()) {
            fail("Target file could not be deleted. Not closed?");
        }
        if (!out300.delete()) {
            fail("Target file could not be deleted. Not closed?");
        }
    }

    /**
     * Returns the base directory to use for the tests.
     * @return the base directory
     */
    private File loadTestFile(final String file) {
        try {
            return Paths.get(this.getClass().getClassLoader().getResource(file).toURI()).toFile();
        } catch (final URISyntaxException e) {
            fail("Could no load file : "+file);
        }
        return null;
    }

}
