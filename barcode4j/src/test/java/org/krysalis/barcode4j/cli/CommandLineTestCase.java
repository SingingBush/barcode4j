/*
 * $Id: CommandLineTestCase.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
 */
public class CommandLineTestCase extends AbstractBarcodeTestCase {

    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;
    private ExitHandlerForTests exitHandler;

    /**
     * @see junit.framework.TestCase#Constructor(String)
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
        File cfgFile = new File(getBaseDir(), "src/test/xml/good-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(),
            "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be 0", 0, this.exitHandler.getLastExitCode());
    }

    public void testBadConfigFile() throws Exception {
        File cfgFile = new File(getBaseDir(), "src/test/xml/bad-cfg.xml");
        final String[] args = {"-c", cfgFile.getAbsolutePath(),
            "9771422985503+00006"};
        callCLI(args);
        assertEquals("Exit code must be -3", -3, this.exitHandler.getLastExitCode());
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
