/*
 * Copyright 2002-2005,2009 Jeremias Maerki.
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
 * @version $Id: Code128Bean.java,v 1.9 2009-02-18 16:09:04 jmaerki Exp $
 */
public class Code128Bean extends AbstractBarcodeBean {

    /** The default module width for Code 128. */
    protected static final double DEFAULT_MODULE_WIDTH = 0.21f; //mm

    /** Default codeset. */
    protected static final int DEFAULT_CODESET = Code128Constants.CODESET_ALL;

    /** Codeset used to encode the message. */
    private int codeset = DEFAULT_CODESET;

    /** Create a new instance. */
    public Code128Bean() {
        this.moduleWidth = DEFAULT_MODULE_WIDTH;
        setQuietZone(10 * this.moduleWidth);
        setVerticalQuietZone(0); //1D barcodes don't have vertical quiet zones
    }

    /**
     * Sets the codesets to use. This can be used to restrict the Code 128 codesets
     * if an application requires that.
     * @param codeset the codesets to use (see {@link Code128Constants}.CODESET_*)
     */
    public void setCodeset(int codeset) {
        if (codeset == 0) {
            throw new IllegalArgumentException("At least one codeset must be allowed");
        }
        this.codeset = codeset;
    }

    /**
     * Returns the codeset to be used.
     * @return the codeset (see {@link Code128Constants}.CODESET_*)
     */
    public int getCodeset() {
        return this.codeset;
    }

    /** {@inheritDoc} */
    protected boolean hasFontDescender() {
        return true;
    }

    /** {@inheritDoc} */
    public double getBarWidth(int width) {
        if ((width >= 1) && (width <= 4)) {
            return width * moduleWidth;
        } else {
            throw new IllegalArgumentException("Only widths 1 and 2 allowed");
        }
    }

    /** {@inheritDoc} */
    public BarcodeDimension calcDimensions(String msg) {
        Code128LogicImpl impl = createLogicImpl();
        int msgLen = 0;

        msgLen = impl.createEncodedMessage(msg).length + 1;

        final double width = ((msgLen * 11) + 13) * getModuleWidth();
        final double qz = (hasQuietZone() ? quietZone : 0);
        final double vqz = (hasQuietZone() ? quietZoneVertical.doubleValue() : 0);

        return new BarcodeDimension(width, getHeight(),
                width + (2 * qz), getHeight() + (2 * vqz),
                qz, vqz);
    }

    private Code128LogicImpl createLogicImpl() {
        return new Code128LogicImpl(getCodeset());
    }

    /** {@inheritDoc} */
    public void generateBarcode(CanvasProvider canvas, String msg) {
        if ((msg == null) || (msg.length() == 0)) {
            throw new NullPointerException("Parameter msg must not be empty");
        }

        ClassicBarcodeLogicHandler handler =
                new DefaultCanvasLogicHandler(this, new Canvas(canvas));
        //handler = new LoggingLogicHandlerProxy(handler);

        Code128LogicImpl impl = createLogicImpl();
        impl.generateBarcodeLogic(handler, msg);
    }

}