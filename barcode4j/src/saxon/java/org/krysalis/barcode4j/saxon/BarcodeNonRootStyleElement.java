/*
 * Copyright 2003,2004 Jeremias Maerki.
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
package org.krysalis.barcode4j.saxon;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import com.icl.saxon.Context;
import com.icl.saxon.style.StyleElement;

/**
 * Non-root barcode elements.
 * 
 * @author Jeremias Maerki
 */
public class BarcodeNonRootStyleElement extends StyleElement {

    /**
     * @see com.icl.saxon.style.StyleElement#prepareAttributes()
     */
    public void prepareAttributes() throws TransformerConfigurationException {
        //nop
    }

    /**
     * @see com.icl.saxon.style.StyleElement#process(com.icl.saxon.Context)
     */
    public void process(Context context) throws TransformerException {
        //nop
    }

}
