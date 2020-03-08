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
package org.krysalis.barcode4j.impl.code128;

/**
 * This interface is implemented by classes that encode a Code128 message into
 * an integer array representing character set indexes.
 *
 * @author Jeremias Maerki
 * @version $Id: Code128Encoder.java,v 1.2 2009-02-18 16:09:05 jmaerki Exp $
 */
public interface Code128Encoder {

    /**
     * Encodes a valid Code 128 message to an array of character set indexes.
     * @param msg the message to encode
     * @return the requested array of indexes
     */
    int[] encode(String msg);

}
