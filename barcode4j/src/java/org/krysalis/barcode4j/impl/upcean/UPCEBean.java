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

import org.krysalis.barcode4j.BarcodeDimension;

/**
 * This class is an implementation of the UPC-E barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: UPCEBean.java,v 1.1 2004-09-12 17:57:52 jmaerki Exp $
 */
public class UPCEBean extends UPCEANBean {

    /** @see org.krysalis.barcode4j.impl.upcean.UPCEAN */
    public UPCEANLogicImpl createLogicImpl() {
        return new UPCELogicImpl(getChecksumMode());
    }

    /** @see org.krysalis.barcode4j.impl.upcean.UPCEAN */
    public BarcodeDimension calcDimensions(String msg) {
        double width = 3 * moduleWidth; //left guard
        width += 6 * 7 * moduleWidth;
        width += 6 * moduleWidth; //right guard
        width += supplementalWidth(msg);
        final double qz = (hasQuietZone() ? quietZone : 0);
        return new BarcodeDimension(width, getHeight(), 
                width + (2 * qz), getHeight(), 
                quietZone, 0.0);
    }



}