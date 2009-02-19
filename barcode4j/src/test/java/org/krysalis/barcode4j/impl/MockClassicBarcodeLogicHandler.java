/*
 * Copyright 2002-2004,2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.impl;

import org.krysalis.barcode4j.BarGroup;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;

/**
 * ClassicBarcodeHandler generating a String representation of the barcode for
 * easy verification in tests.
 *
 * @author Jeremias Maerki
 * @version $Id: MockClassicBarcodeLogicHandler.java,v 1.5 2009-02-19 10:14:54 jmaerki Exp $
 */
public class MockClassicBarcodeLogicHandler
            implements ClassicBarcodeLogicHandler {

    private StringBuffer sb;
    private boolean dumpBars = true;
    private boolean dumpHumanReadable;

    /**
     * Creates a new instance.
     * @param sb the StringBuffer receiving the serialized events.
     */
    public MockClassicBarcodeLogicHandler(StringBuffer sb) {
        this(sb, true, false);
    }

    /**
     * Creates a new instance.
     * @param sb the StringBuffer receiving the serialized events.
     * @param dumpHumanReadable true if the human-readable message should be included in the output
     */
    public MockClassicBarcodeLogicHandler(StringBuffer sb, boolean dumpHumanReadable) {
        this(sb, true, dumpHumanReadable);
    }

    /**
     * Creates a new instance.
     * @param sb the StringBuffer receiving the serialized events.
     * @param dumpBars true if the bars should be included in the output
     * @param dumpHumanReadable true if the human-readable message should be included in the output
     */
    public MockClassicBarcodeLogicHandler(StringBuffer sb,
            boolean dumpBars, boolean dumpHumanReadable) {
        this.sb = sb;
        this.dumpBars = dumpBars;
        this.dumpHumanReadable = dumpHumanReadable;
    }

    /** {@inheritDoc} */
    public void startBarGroup(BarGroup type, String submsg) {
        sb.append("<SBG:");
        sb.append(type.getName());
        sb.append(":");
        sb.append(submsg);
        sb.append(">");
    }

    /** {@inheritDoc} */
    public void addBar(boolean black, int weight) {
        if (dumpBars) {
            if (black) {
                sb.append("B");
            } else {
                sb.append("W");
            }
            sb.append(weight);
        }
    }

    /** {@inheritDoc} */
    public void endBarGroup() {
        sb.append("</SBG>");
    }

    /** {@inheritDoc} */
    public void startBarcode(String msg, String formattedMsg) {
        if (this.dumpHumanReadable) {
            sb.append("<BC:").append(formattedMsg).append(">");
        } else {
            sb.append("<BC>");
        }
    }

    /** {@inheritDoc} */
    public void endBarcode() {
        sb.append("</BC>");
    }

}
