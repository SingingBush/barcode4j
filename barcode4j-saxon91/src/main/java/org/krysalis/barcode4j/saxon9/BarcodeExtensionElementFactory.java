/*
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
package org.krysalis.barcode4j.saxon9;

import net.sf.saxon.style.ExtensionElementFactory;

/**
 * This class represents the element factory for the barcode extension for Saxon.
 * <p>
 * Later releases of Saxon are split into 3 versions:
 * <ul>
 *   <li>Saxon-HE (Home Edition) available on maven central</li>
 *   <li>Saxon-PE (Professional Edition) available from Saxonica maven repo &amp; requires license</li>
 *   <li>Saxon-EE (Enterprise Edition) available from Saxonica maven repo &amp; requires license</li>
 * </ul>
 * </p>
 * <p>
 * Saxonica only provide support for element extensibility in the EE and PE releases.
 * From version 10 onward, instead of implementing "net.sf.saxon.style.ExtensionElementFactory"
 * use "com.saxonica.xsltextn.ExtensionElementFactory"
 * </p>
 * @author Jeremias Maerki
 * @see net.sf.saxon.style.ExtensionElementFactory
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
