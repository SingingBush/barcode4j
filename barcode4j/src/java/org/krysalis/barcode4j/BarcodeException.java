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
package org.krysalis.barcode4j;

import org.apache.avalon.framework.CascadingException;

/**
 * Base exception class for Barcodes.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeException.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class BarcodeException extends CascadingException {

    /**
     * Constructor for BarcodeException.
     * 
     * @param message the detail message for this exception.
     */
    public BarcodeException(String message) {
        super(message);
    }

    /**
     * Constructor for BarcodeException.
     * 
     * @param message the detail message for this exception.
     * @param throwable the root cause of the exception
     */
    public BarcodeException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
