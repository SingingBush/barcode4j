/*
 * Copyright 2003-2012 Jeremias Maerki.
 * Copyright 2020-2024 Samael Bate (singingbush)
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

import net.sf.saxon.expr.Expression;
import net.sf.saxon.style.Compilation;
import net.sf.saxon.style.ComponentDeclaration;
import net.sf.saxon.style.StyleElement;
import net.sf.saxon.trans.XPathException;

/**
 * Non-root barcode elements.
 *
 * @author Jeremias Maerki &amp; Samael Bate (singingbush)
 */
public class BarcodeNonRootStyleElement extends StyleElement {

    /**
     * @see StyleElement#prepareAttributes()
     */
    @Override
    public void prepareAttributes() {
        //nop
    }

    @Override
    public Expression compile(Compilation compilation, ComponentDeclaration decl) throws XPathException {
        return null;
    }

    /*
     * @see net.sf.saxon.style.StyleElement#compile(net.sf.saxon.expr.instruct.Executable)
     */
//    @Override
//    public Expression compile(Executable exec) throws XPathException {
//        return null;
//    }

}
