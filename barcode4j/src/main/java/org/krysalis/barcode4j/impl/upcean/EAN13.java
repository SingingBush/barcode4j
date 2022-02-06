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
package org.krysalis.barcode4j.impl.upcean;

/**
 * This class implements the EAN13 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: EAN13.java,v 1.1 2004-09-12 17:57:52 jmaerki Exp $
 */
public class EAN13 extends UPCEAN {

    /** Create a new instance. */
    public EAN13() {
        this.bean = new EAN13Bean();
    }

}