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

	private EAN128LogicImpl impl;
    private ChecksumMode checksumMode = ChecksumMode.CP_AUTO;
	private String template = null;
	public final static char defaultGroupSeparator = '\u001D';
	private char groupSeparator = defaultGroupSeparator; //GroupSeperator not Code128LogicImpl.FNC_1; 
	public final static char defaultCheckDigitMarker = '\u00F0';
	private char checkDigitMarker = defaultCheckDigitMarker; 
	private boolean omitBrackets = false;

    /** Create a new instance. */
    public EAN128Bean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
		impl = new EAN128LogicImpl(checksumMode, template, groupSeparator);//TODO ???? checkDigitMarker, omitBrackets  
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
	 * @return
	 */
	public char getGroupSeparator() {
		return groupSeparator;
	}

	/**
	 * @return
	 */
	public EAN128LogicImpl getImpl() {
		return impl;
	}

	/**
	 * @return
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param c
	 */
	public void setGroupSeparator(char c) {
		groupSeparator = c;
		impl.setGroupSeparator(c);
	}
	public void setGroupSeparator(String s) {
		if (s != null && s.length()>0){
			if (s.length() > 1)
				s = s.trim();
			setGroupSeparator(s.charAt(0));
		}
		//TODO Error if not as single char
	}

	/**
	 * @param impl
	 */
	public void setImpl(EAN128LogicImpl impl) {
		this.impl = impl;
	}

	/**
	 * @param string
	 */
	public void setTemplate(String string) {
		template = string;
		impl.setTemplate(string);
	}

	/**
	 * @return
	 */
	public char getCheckDigitMarker() {
		return checkDigitMarker;
	}

	/**
	 * @param c
	 */
	public void setCheckDigitMarker(char c) {
		checkDigitMarker = c;
		impl.setCheckDigitMarker(c); 
	}
	public void setCheckDigitMarker(String s) {
		if (s != null && s.length()>0){
			if (s.length() > 1)
				s = s.trim();
			setCheckDigitMarker(s.charAt(0));
		}
		//TODO Error if not as single char
	}

	/**
	 * @return
	 */
	public boolean isOmitBrackets() {
		return omitBrackets;
	}

	/**
	 * @param b
	 */
	public void setOmitBrackets(boolean b) {
		omitBrackets = b;
		impl.setOmitBrackets(b);
	}
}