/*
 * $Id: AdvancedConsoleLogger.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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

import java.io.PrintStream;

import org.apache.avalon.framework.logger.Logger;

/**
 * Special Logger implementation that can split output between stdout and stderr
 * based on the log level and can omit the log level prefix.
 * 
 * @author Jeremias Maerki
 */
public class AdvancedConsoleLogger implements Logger {

    /** Log level: debug */
    public static final int LEVEL_DEBUG = 0;

    /** Log level: info */
    public static final int LEVEL_INFO = 1;

    /** Log level: warnings */
    public static final int LEVEL_WARN = 2;

    /** Log level: errors */
    public static final int LEVEL_ERROR = 3;

    /** Log level: fatal errors */
    public static final int LEVEL_FATAL = 4;

    /** Log level: disabled */
    public static final int LEVEL_DISABLED = 5;

    private static final String[] LEVEL_STRINGS = 
            {"[DEBUG] ", "[INFO] ", "[WARN] ", "[ERROR] ", "[FATAL] "};


    private int logLevel;
    
    private boolean prefix;
    
    private PrintStream out;
    private PrintStream err;
    
    /**
     * Constructor will full configurability.
     * @param logLevel One of the AdvancedConsoleLogger.LEVEL_* constants.
     * @param prefix false disables "[DEBUG] ", "[INFO] " prefixes
     * @param out PrintStream to use for stdout/System.out
     * @param err PrintStream to use for stderr/System.err
     */
    public AdvancedConsoleLogger(int logLevel, boolean prefix, PrintStream out, PrintStream err) {
        this.logLevel = logLevel;
        this.prefix = prefix;
        this.out = out;
        this.err = err;
    }

    /**
     * Default constructor. Same behaviour as Avalon's ConsoleLogger.
     */
    public AdvancedConsoleLogger() {
        this(LEVEL_DEBUG, true, System.out, System.err);
    }

    private void logMessage(String msg, Throwable t, int logLevel) {
        if (logLevel >= this.logLevel) {
            PrintStream stream = (logLevel >= LEVEL_ERROR ? err : out);
            if (prefix) {
                stream.print(LEVEL_STRINGS[logLevel]);
            }
            stream.println(msg);

            if (t != null) {
                t.printStackTrace(stream);
            }
        }
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#debug(String)
     */
    public void debug(String msg) {
        debug(msg, null);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#debug(String, Throwable)
     */
    public void debug(String msg, Throwable t) {
        logMessage(msg, t, LEVEL_DEBUG);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isDebugEnabled()
     */
    public boolean isDebugEnabled() {
        return (logLevel <= LEVEL_DEBUG);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#info(String)
     */
    public void info(String msg) {
        info(msg, null);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#info(String, Throwable)
     */
    public void info(String msg, Throwable t) {
        logMessage(msg, t, LEVEL_INFO);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isInfoEnabled()
     */
    public boolean isInfoEnabled() {
        return (logLevel <= LEVEL_INFO);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#warn(String)
     */
    public void warn(String msg) {
        warn(msg, null);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#warn(String, Throwable)
     */
    public void warn(String msg, Throwable t) {
        logMessage(msg, t, LEVEL_WARN);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isWarnEnabled()
     */
    public boolean isWarnEnabled() {
        return (logLevel <= LEVEL_WARN);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#error(String)
     */
    public void error(String msg) {
        error(msg, null);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#error(String, Throwable)
     */
    public void error(String msg, Throwable t) {
        logMessage(msg, t, LEVEL_ERROR);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isErrorEnabled()
     */
    public boolean isErrorEnabled() {
        return (logLevel <= LEVEL_ERROR);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#fatalError(String)
     */
    public void fatalError(String msg) {
        fatalError(msg, null);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#fatalError(String, Throwable)
     */
    public void fatalError(String msg, Throwable t) {
        logMessage(msg, t, LEVEL_FATAL);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#isFatalErrorEnabled()
     */
    public boolean isFatalErrorEnabled() {
        return (logLevel <= LEVEL_FATAL);
    }

    /**
     * @see org.apache.avalon.framework.logger.Logger#getChildLogger(String)
     */
    public Logger getChildLogger(String name) {
        return this;
    }

}
