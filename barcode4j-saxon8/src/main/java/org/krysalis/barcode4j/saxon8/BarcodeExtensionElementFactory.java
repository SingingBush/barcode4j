/*
 * Copyright 2003-2004 Jeremias Maerki.
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

import net.sf.saxon.style.ExtensionElementFactory;

/**
 * This class represents the element factory for the barcode extension for Saxon.
 * <p>
 * Later releases of Saxon are split into 3 versions:
 * </p>
 * <ul>
 *   <li>Saxon-HE (Home Edition) available on maven central</li>
 *   <li>Saxon-PE (Professional Edition) manual download &amp; requires license</li>
 *   <li>Saxon-EE (Enterprise Edition) manual download &amp; requires license</li>
 * </ul>
 * <p>
 * The net.sf.saxon.style.ExtensionElementFactory class is not available in Saxon-HE as Saxonica
 * only provide support for element extensibility in the EE and PE releases.
 * </p>
 * @see net.sf.saxon.style.ExtensionElementFactory
 * @author Jeremias Maerki
 * @version $Id: BarcodeExtensionElementFactory.java,v 1.2 2004-09-04 20:25:55 jmaerki Exp $
 */
public class BarcodeExtensionElementFactory implements ExtensionElementFactory {

    /**
     * @see net.sf.saxon.style.ExtensionElementFactory#getExtensionClass(java.lang.String)
     */
    @Override
    public Class getExtensionClass(String localName) {
        return localName.equals("barcode") ? BarcodeStyleElement.class : BarcodeNonRootStyleElement.class;
    }

}
