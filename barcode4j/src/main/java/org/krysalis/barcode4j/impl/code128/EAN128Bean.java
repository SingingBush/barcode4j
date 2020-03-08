/*
 * Copyright 2002-2004 Jeremias Maerki.
 * Copyright 2005 Jeremias Maerki, Dietmar Bürkle.
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

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.DefaultCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * This class is an implementation of the Code 128 barcode.
 * 
 * @author Jeremias Maerki, Dietmar Bürkle
 */
public class EAN128Bean extends Code128Bean {

    /** Defines the default group separator character */
    public static final char DEFAULT_GROUP_SEPARATOR = '\u001D'; //ASCII: GS
    /** Defines the default character for the check digit marker */
    public static final char DEFAULT_CHECK_DIGIT_MARKER = '\u00F0';

    private EAN128LogicImpl impl;

    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
    private String template = null;
    private char groupSeparator = DEFAULT_GROUP_SEPARATOR; //GroupSeperator not Code128LogicImpl.FNC_1; 
    private char checkDigitMarker = DEFAULT_CHECK_DIGIT_MARKER; 
    private boolean omitBrackets = false;

    /** Create a new instance. */
    public EAN128Bean() {
        super();
        impl = new EAN128LogicImpl(checksumMode, template, groupSeparator);
    }
    
    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        int msgLen = impl.getEncodedMessage(msg).length + 1; 
        //TODO If the output is able to calculate text lenghts (e.g. awt, fop), and 
        //the human readable part is longer then barcode the size should be enlarged!
        final double width = ((msgLen * 11) + 13) * getModuleWidth();
        final double qz = (hasQuietZone() ? quietZone : 0);
        return new BarcodeDimension(width, getHeight(), 
                width + (2 * qz), getHeight(), 
                quietZone, 0.0);
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#generateBarcode(CanvasProvider, String)
     */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        ClassicBarcodeLogicHandler handler = 
                new DefaultCanvasLogicHandler(this, new Canvas(canvas));
        //handler = new LoggingLogicHandlerProxy(handler);
        
        impl.generateBarcodeLogic(handler, msg);
    }
    
    /**
     * Sets the checksum mode
     * @param mode the checksum mode
     */
    public void setChecksumMode(ChecksumMode mode) {
        this.checksumMode = mode;
        impl.setChecksumMode(mode);
    }

    /**
     * Returns the current checksum mode.
     * @return ChecksumMode the checksum mode
     */
    public ChecksumMode getChecksumMode() {
        return this.checksumMode;
    }


    /**
     * @return the group separator character
     */
    public char getGroupSeparator() {
        return groupSeparator;
    }

    /**
     * Sets the group separator character. Normally, either ASCII GS or 0xF1 is used.
     * @param c the group separator character.
     */
    public void setGroupSeparator(char c) {
        groupSeparator = c;
        impl.setGroupSeparator(c);
    }
    
    /*
    public EAN128LogicImpl getImpl() {
        return impl;
    }

    public void setImpl(EAN128LogicImpl impl) {
        this.impl = impl;
    }*/

    /**
     * @return the message template with the fields for the EAN message
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the message template with the fields for the EAN message.
     * <p>
     * The format of the templates here is a repeating set of AI number (in brackets)
     * followed by a field description. The allowed data types are "n" (numeric), 
     * "an" (alpha-numeric), "d" (date) and "cd" (check digit). Examples: "n13" defines a numeric
     * field with exactly 13 digits. "n13+cd" defines a numeric field with exactly 13 digits plus
     * a check digit. "an1-9" defines an alpha-numeric field with 1 to 9 characters.
     * @param string a template like "(01)n13+cd(421)n3+an1-9(10)an1-20"
     */
    public void setTemplate(String string) {
        template = string;
        impl.setTemplate(string);
    }

    /**
     * @return the character used as the check digit marker.
     */
    public char getCheckDigitMarker() {
        return checkDigitMarker;
    }

    /**
     * Sets the character that will be used as the check digit marker.
     * @param c the character for the check digit marker
     */
    public void setCheckDigitMarker(char c) {
        checkDigitMarker = c;
        impl.setCheckDigitMarker(c); 
    }
    
    /**
     * @return true if the brackets in the human-readable part should be omitted
     */
    public boolean isOmitBrackets() {
        return omitBrackets;
    }

    /**
     * Indicates whether brackets should be used in the human-readable part around the AIs.
     * @param b true if the brackets in the human-readable part should be omitted
     */
    public void setOmitBrackets(boolean b) {
        omitBrackets = b;
        impl.setOmitBrackets(b);
    }
}