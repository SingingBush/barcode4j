/*
 * $Id: ExitHandlerForTests.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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

/**
 * Special exit handler for tests that does not call System.exit().
 * 
 * @author Jeremias Maerki
 */
public class ExitHandlerForTests extends AbstractExitHandler {

    private String lastMsg;
    private Throwable lastThrowable;
    private int lastExitCode = 0;

    public void reset() {
        this.lastMsg = null;
        this.lastThrowable = null;
        this.lastExitCode = 0;
    }

    /**
     * Returns the last recorded exit code.
     * @return the exit code
     */
    public int getLastExitCode() {
        return lastExitCode;
    }

    /**
     * Returns the last recorded error message.
     * @return the error message
     */
    public String getLastMsg() {
        return lastMsg;
    }

    /**
     * Returns the last recorded Throwable.
     * @return a Throwable
     */
    public Throwable getLastThrowable() {
        return lastThrowable;
    }

    /** {@inheritDoc} */
    public void failureExit(Main app, String msg, Throwable t, int exitCode) {
        super.failureExit(app, msg, t, exitCode);
        this.lastMsg = msg;
        this.lastThrowable = t;
        this.lastExitCode = exitCode;
        throw new SimulateVMExitError();
    }

    /** {@inheritDoc} */
    public void successfulExit(Main app) {
        super.successfulExit(app);
        this.lastMsg = null;
        this.lastThrowable = null;
        this.lastExitCode = 0;
        throw new SimulateVMExitError();
    }

}
