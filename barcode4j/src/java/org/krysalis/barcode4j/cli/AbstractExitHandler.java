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

import org.apache.avalon.framework.ExceptionUtil;

/**
 * Abstract base class for an exit handler for the CLI.
 * 
 * @author Jeremias Maerki
 * @version $Id: AbstractExitHandler.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
 */
public abstract class AbstractExitHandler implements ExitHandler {

    /** @see org.krysalis.barcode4j.cli.ExitHandler */
    public void successfulExit(Main app) {
        //nop
    }

    /** @see org.krysalis.barcode4j.cli.ExitHandler */
    public void failureExit(Main app, String msg, Throwable t, int exitCode) {
        if (msg == null) {
            throw new NullPointerException("msg must not be null");
        }
        if (exitCode == 0) {
            throw new IllegalArgumentException("exitCode must not be zero");
        }
        app.printAppHeader();
        Main.stderr.println(msg);
        if (t != null) {
            Main.stderr.println(ExceptionUtil.printStackTrace(t));
        }
    }

}
