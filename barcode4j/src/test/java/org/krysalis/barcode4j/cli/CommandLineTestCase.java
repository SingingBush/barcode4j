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

import org.apache.avalon.framework.ExceptionUtil;
import org.krysalis.barcode4j.AbstractBarcodeTestCase;

/**
 * Tests the command line application
 * @author Jeremias Maerki
 * @version $Id: CommandLineTestCase.java,v 1.3 2004-10-02 14:58:23 jmaerki Exp $
 */
public class CommandLineTestCase extends AbstractBarcodeTestCase {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private ExitHandlerForTests exitHandler;

    /**
     * @see junit.framework.TestCase#TestCase(String)
     */
    public CommandLineTestCase(String name) {
        super(name);
    }

    private void dumpResults() throws Exception {
        System.out.println("Msg: " + this.exitHandler.getLastMsg());
        System.out.println("Exit code: " + this.exitHandler.getLastExitCode());
        if (this.exitHandler.getLastThrowable() != null) {
            System.out.println(ExceptionUtil.printStackTrace(
                this.exitHandler.getLastThrowable()));
        }
        System.out.println("--- stdout (" + this.out.size() + ") ---");
        System.out.println(new String(this.out.toByteArray(), "US-ASCII"));
        System.out.println("--- stderr (" + this.err.size() + ") ---");
        System.out.println(new String(this.err.toByteArray(), "US-ASCII"));
        System.out.println("---");
    }

    private void callCLI(String[] args) {
        Main app = new Main();
        try {
            app.handleCommandLine(args);
        } catch (SimulateVMExitError se) {
            //ignore
        }
    }

    /** {@inheritDoc} */
    protected void setUp() throws Exception {
        this.out = new ByteArrayOutputStream();
        this.err = new ByteArrayOutputStream();
        Main.stdout = new PrintStream(this.out);
        Main.stderr = new PrintStream(this.err);
        this.exitHandler = new ExitHandlerForTests();
        Main.setExitHandler(this.exitHandler);
    }
    
    public void testSVG() throws Exception {
        final String[] args = {"-s", "ean13", "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("No output", this.out.size() > 0);
        assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    public void testEPS() throws Exception {
        final String[] args = {"-s", "ean13", "-f", "eps", "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("No output", this.out.size() > 0);
        assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    public void testBitmapJPEG() throws Exception {
        final String[] args = {"-s", "ean13", "-f", "image/jpeg", "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("No output", this.out.size() > 0);
        assertTrue("No output on stderr expected", this.err.size() == 0);
    }

    public void testNoArgs() throws Exception {
        final String[] args = new String[0];
        callCLI(args);
        assertEquals("Exit code must be -2", -2, this.exitHandler.getLastExitCode());
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("CLI help expected on stdout", this.out.size() > 0);
        assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    public void testUnknownArg() throws Exception {
        final String[] args = {"--badArgument"};
        callCLI(args);
        assertEquals("Exit code must be -2", -2, this.exitHandler.getLastExitCode());
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("CLI help expected on stdout", this.out.size() > 0);
        assertTrue("Error message expected on stderr", this.err.size() > 0);
    }
    
    public void testWrongConfigFile() throws Exception {
        final String[] args = {"-c", "NonExistingConfigFile", "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be -3", -3, this.exitHandler.getLastExitCode());
        assertNotNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("In case of error stdout may only be written to if there's "
            + "a problem with the command-line", this.out.size() == 0);
        assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    public void testValidConfigFile() throws Exception {
        final File cfgFile = loadTestFile("xml/good-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(), "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
    }

    public void testBadConfigFile() throws Exception {
        final File cfgFile = loadTestFile("xml/bad-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(), "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be -6", -6, this.exitHandler.getLastExitCode());
        assertNotNull(this.exitHandler.getLastMsg());
        assertNotNull(this.exitHandler.getLastThrowable());
        assertTrue("In case of error stdout may only be written to if there's "
            + "a problem with the command-line", this.out.size() == 0);
        assertTrue("Error message expected on stderr", this.err.size() > 0);
    }

    public void testToFile() throws Exception {
        File out = File.createTempFile("krba", ".tmp");
        final String[] args = {"-s", "ean-13", "-o", out.getAbsolutePath(),
                 "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertNull(this.exitHandler.getLastMsg());
        assertNull(this.exitHandler.getLastThrowable());
        assertTrue("Application header expected on stdout",
            this.out.size() > 0);
        assertTrue("No output expected on stderr", this.err.size() == 0);
        assertTrue("Target file does not exist", out.exists());
        assertTrue("Target file must not be empty", out.length() > 0);
        if (!out.delete()) {
            fail("Target file could not be deleted. Not closed?");
        } 
    }

    public void testDPI() throws Exception {
        File out100 = File.createTempFile("krba", ".tmp");
        final String[] args100 = {"-s", "ean-13", 
                 "-o", out100.getAbsolutePath(),
                 "-f", "jpeg", 
                 "-d", "100", "9771422985503+00006"};
        callCLI(args100);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertTrue("Target file does not exist", out100.exists());

        File out300 = File.createTempFile("krba", ".tmp");
        final String[] args300 = {"-s", "ean-13", 
                 "-o", out300.getAbsolutePath(),
                 "-f", "jpeg",
                 "--dpi", "300", "9771422985503+00006"};
        callCLI(args300);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
        assertTrue("Target file does not exist", out300.exists());

        assertTrue("300dpi file must be greater than the 100dpi file", 
            out300.length() > out100.length());
        if (!out100.delete()) {
            fail("Target file could not be deleted. Not closed?");
        } 
        if (!out300.delete()) {
            fail("Target file could not be deleted. Not closed?");
        } 
    }

}
