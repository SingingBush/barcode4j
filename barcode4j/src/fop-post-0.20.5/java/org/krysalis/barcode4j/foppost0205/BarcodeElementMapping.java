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
package org.krysalis.barcode4j.foppost0205;

import org.apache.fop.fo.FObj.Maker;

/**
 * Class representing bc:barcode pseudo flow object.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeElementMapping.java,v 1.2 2004-09-04 20:25:58 jmaerki Exp $
 */
public class BarcodeElementMapping
    extends org.krysalis.barcode4j.fop0205.BarcodeElementMapping {

    /** @see org.krysalis.barcode4j.fop0205.BarcodeElementMapping#getBarcodeElementMaker() */
    protected Maker getBarcodeElementMaker() {
        return BarcodeElement.maker();
    }

}
