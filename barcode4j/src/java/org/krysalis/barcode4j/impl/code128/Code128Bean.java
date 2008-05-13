/*
 * Copyright 2002-2005 Jeremias Maerki.
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
import org.krysalis.barcode4j.ClassicBarcodeLogicHandler;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.DefaultCanvasLogicHandler;
import org.krysalis.barcode4j.output.Canvas;
import org.krysalis.barcode4j.output.CanvasProvider;

/**
 * This class is an implementation of the Code 128 barcode.
 * 
 * @author Jeremias Maerki
 * @version $Id: Code128Bean.java,v 1.7 2008-05-13 13:00:45 jmaerki Exp $
 */
public class Code128Bean extends AbstractBarcodeBean {

    /** The default module width for Code 128. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.21f; //mm

    /** Create a new instance. */
    public Code128Bean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        setQuietZone(10 * this.moduleWidth);
        setVerticalQuietZone(0); //1D barcodes don't have vertical quiet zones
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#hasFontDescender()
     */
    protected boolean hasFontDescender() {
        return true;
    }
    
    /**
     * @see org.krysalis.barcode4j.impl.AbstractBarcodeBean#getBarWidth(int)
     */
    public double getBarWidth(int width) {
        if ((width >= 1) && (width <= 4)) {
            return width * moduleWidth;
        } else {
            throw new IllegalArgumentException("Only widths 1 and 2 allowed");
        }
    }

    /**
     * @see org.krysalis.barcode4j.BarcodeGenerator#calcDimensions(String)
     */
    public BarcodeDimension calcDimensions(String msg) {
        Code128LogicImpl impl = new Code128LogicImpl();
        int msgLen = impl.createEncodedMessage(msg).length + 1;
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
        
        Code128LogicImpl impl = new Code128LogicImpl();
        impl.generateBarcodeLogic(handler, msg);
    }


}