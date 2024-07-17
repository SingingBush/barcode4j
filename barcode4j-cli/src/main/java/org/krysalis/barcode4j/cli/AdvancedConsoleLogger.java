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

/**
 * Special Logger implementation that can split output between stdout and stderr
 * based on the log level and can omit the log level prefix.
 *
 * @author Jeremias Maerki
 * @version $Id: AdvancedConsoleLogger.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
 */
@Deprecated // only used in CLI module, remove in another commit
public class AdvancedConsoleLogger {

    public static final int LEVEL_DEBUG = 0;

    public static final int LEVEL_INFO = 1;

    public static final int LEVEL_ERROR = 2;

    private static final String[] LEVEL_STRINGS = {"[DEBUG] ", "[INFO] ", "[ERROR] "};


    private final int logLevel;

    private final boolean prefix;

    private final PrintStream out;
    private final PrintStream err;

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
            final PrintStream stream = (logLevel >= LEVEL_ERROR ? err : out);
            if (prefix) {
                stream.print(LEVEL_STRINGS[logLevel]);
            }
            stream.println(msg);

            if (t != null) {
                t.printStackTrace(stream);
            }
        }
    }

    public void debug(String msg) {
        logMessage(msg, null, LEVEL_DEBUG);
    }

    public boolean isDebugEnabled() {
        return (logLevel <= LEVEL_DEBUG);
    }

    public void info(String msg) {
        logMessage(msg, null, LEVEL_INFO);
    }

}
