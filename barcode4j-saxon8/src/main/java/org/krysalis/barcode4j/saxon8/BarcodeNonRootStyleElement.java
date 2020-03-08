/*
 * Copyright 2003-2004,2007 Jeremias Maerki.
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
package org.krysalis.barcode4j.saxon8;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.trans.XPathException;

/**
 * Non-root barcode elements.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeNonRootStyleElement.java,v 1.3 2007-01-15 11:12:33 jmaerki Exp $
 */
public class BarcodeNonRootStyleElement extends StyleElement {

    /**
     * @see net.sf.saxon.style.StyleElement#prepareAttributes()
     */
    public void prepareAttributes() throws XPathException {
        //nop
    }

    /**
     * @see net.sf.saxon.style.StyleElement#compile(net.sf.saxon.instruct.Executable)
     */
    public Expression compile(Executable exec) throws XPathException {
        return null;
    }

}
