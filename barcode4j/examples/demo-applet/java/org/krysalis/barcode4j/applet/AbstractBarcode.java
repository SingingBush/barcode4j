/*
 * Copyright 2004 Jeremias Maerki.
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

package org.krysalis.barcode4j.applet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;

/**
 * Abstract base class for a Swing component for rendering barcode in a Swing
 * application.
 * 
 * @author Jeremias Maerki
 */
public abstract class AbstractBarcode extends JComponent {

    private BarcodeGenerator bargen;
    private String msg;
    private Java2DCanvasProvider canvas;
    private BarcodeDimension bardim;
    private List errorListeners = new java.util.LinkedList();

    public void addErrorListener(BarcodeErrorListener listener) {
        errorListeners.add(listener);
    }
    
    public void fireSuccessNotification() {
        Iterator i = errorListeners.iterator();
        while (i.hasNext()) {
            ((BarcodeErrorListener)i.next()).notifySuccess();
        }
    }
    
    public void fireErrorNotification(Exception e) {
        Iterator i = errorListeners.iterator();
        while (i.hasNext()) {
            ((BarcodeErrorListener)i.next()).notifyException(e);
        }
    }
    
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
        //System.out.println("bardim: " + this.bardim);
    }
    
    public void setBarcodeGenerator(BarcodeGenerator bargen) {
        this.bargen = bargen;
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
    
    protected abstract void transformAsNecessary(Graphics2D g2d);
    
    /**
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        //System.out.println("paint");
        if (bargen == null || msg == null) {
            return;
        }
        Graphics2D g2d = (Graphics2D)g;
        if (canvas == null) {
            canvas = new Java2DCanvasProvider(g2d);
        } else {
            canvas.setGraphics2D(g2d);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, 
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        try {
            AffineTransform baktrans = g2d.getTransform();
            try {
                //set up for painting
                transformAsNecessary(g2d);
                g2d.setColor(Color.black);

                //now paint the barcode
                getBarcodeGenerator().generateBarcode(canvas, getMessage());
                fireSuccessNotification();
            } finally {
                g2d.setTransform(baktrans);
            }
        } catch (Exception e) {
            g.setColor(Color.RED);
            g.drawLine(0, 0, getWidth(), getHeight());
            g.drawLine(0, getHeight(), getWidth(), 0);
            fireErrorNotification(e);
        }
    }
}
