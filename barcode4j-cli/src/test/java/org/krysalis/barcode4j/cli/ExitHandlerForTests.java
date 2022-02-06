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

/**
 * Special exit handler for tests that does not call System.exit().
 * 
 * @author Jeremias Maerki
 * @version $Id: ExitHandlerForTests.java,v 1.2 2004-09-04 20:25:59 jmaerki Exp $
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
