/*
 * Copyright 2024
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

package org.krysalis.barcode4j.swing;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * This swing component was ported over from some of the old example code as it useful for displaying barcodes in sing applications
 * @author Samael Bate (singingbush)
 * created on 23/02/2024
 * @since 2.2.3
 */
public class BarcodeComponent extends JComponent {

    private BarcodeGenerator bargen;
    private String msg;
    private Java2DCanvasProvider canvas;
    private BarcodeDimension bardim;

    /** the barcode orientation (0, 90, 180, 270) */
    private int orientation;

    public BarcodeComponent(BarcodeGenerator generator, String msg) {
        this(generator, msg, 0);
    }

    public BarcodeComponent(BarcodeGenerator generator, String msg, int orientation) {
        this.bargen = generator;
        this.msg = msg;
        this.orientation = orientation;
        updateBarcodeDimension();
        repaint();
    }

    // todo: this should take orientation into account
    protected void updateBarcodeDimension() {
        if ((getBarcodeGenerator() != null) && (getMessage() != null)) {
            try {
                this.bardim = getBarcodeGenerator().calcDimensions(getMessage());
            } catch (IllegalArgumentException iae) {
                this.bardim = null;
            }
        } else {
            this.bardim = null;
        }
    }

    public void setBarcodeGenerator(BarcodeGenerator generator) {
        this.bargen = generator;
        updateBarcodeDimension();
        repaint();
    }

    public BarcodeGenerator getBarcodeGenerator() {
        return this.bargen;
    }

    public void setMessage(String msg) {
        if (!msg.equals(this.msg)) {
            this.msg = msg;
            updateBarcodeDimension();
            repaint();
        }
    }

    public String getMessage() {
        return this.msg;
    }

    public BarcodeDimension getBarcodeDimension() {
        return this.bardim;
    }

    private void transformAsNecessary(Graphics2D g2d) {
        if (getBarcodeDimension() != null) {
            double horzScale = getWidth() / getBarcodeDimension().getWidthPlusQuiet();
            double vertScale = getHeight() / getBarcodeDimension().getHeightPlusQuiet();
            double scale;
            double dx = 0;
            double dy = 0;
            if (horzScale < vertScale) {
                scale = horzScale;
                dy = ((getHeight() / scale) - getBarcodeDimension().getHeightPlusQuiet()) / 2;
            } else {
                scale = vertScale;
                dx = ((getWidth() / scale) - getBarcodeDimension().getWidthPlusQuiet()) / 2;
            }
            g2d.scale(scale, scale); //scale for mm to screen pixels
            g2d.translate(dx, dy); //center
//            if (DEBUG) {
//                Color bakCol = g2d.getColor();
//                g2d.setColor(boundingRectCol);
//                g2d.fill(getBarcodeDimension().getBoundingRect());
//                g2d.setColor(contentRectCol);
//                g2d.fill(getBarcodeDimension().getContentRect());
//                g2d.setColor(bakCol);
//            }
        }
    }

    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        if (bargen == null || msg == null) {
            return;
        }
        Graphics2D g2d = (Graphics2D)g;
        if (canvas == null) {
            canvas = new Java2DCanvasProvider(g2d, this.orientation);
        } else {
            canvas.setGraphics2D(g2d);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        try {
            final AffineTransform baktrans = g2d.getTransform();
            try {
                //set up for painting
                transformAsNecessary(g2d);
                g2d.setColor(Color.black);

                //now paint the barcode
                getBarcodeGenerator().generateBarcode(canvas, getMessage());
                //fireSuccessNotification();
            } finally {
                g2d.setTransform(baktrans);
            }
        } catch (Exception e) {
            g.setColor(Color.red);
            g.drawLine(0, 0, getWidth(), getHeight());
            g.drawLine(0, getHeight(), getWidth(), 0);
            //fireErrorNotification(e);
        }
    }
}
