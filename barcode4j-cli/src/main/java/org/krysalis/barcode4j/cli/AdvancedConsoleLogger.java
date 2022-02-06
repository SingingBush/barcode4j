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

import java.io.PrintStream;

import org.apache.avalon.framework.logger.Logger;

/**
 * Special Logger implementation that can split output between stdout and stderr
 * based on the log level and can omit the log level prefix.
 * 
 * @author Jeremias Maerki
 * @version $Id: AdvancedConsoleLogger.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
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
