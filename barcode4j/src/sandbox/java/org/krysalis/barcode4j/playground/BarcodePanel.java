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
package org.krysalis.barcode4j.playground;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.output.CanvasProvider;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * @version $Id: BarcodePanel.java,v 1.3 2006-11-07 16:46:45 jmaerki Exp $
 */
public class BarcodePanel extends JPanel {

    private BarcodeGenerator bargen;
    private String msg;

    public void setBarcode(BarcodeGenerator bargen, String msg) {
        this.bargen = bargen;
        this.msg = msg;
    }

    /**
     * @see javax.swing.JComponent#paintComponent(Graphics)
     */
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Rectangle rect = new Rectangle(30, 30, getWidth() - 60, getHeight() - 60);
        paintBarcode(graphics, rect);
        graphics.setColor(Color.blue);
        graphics.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());

        if (false) {
            Font f = new Font("Tahoma", Font.PLAIN, (int)Math.round(UnitConv.pt2mm(80)));
            graphics.setFont(f);
            graphics.drawString("123 Test Text", 50, 50);

            f = new Font("OCR-B-10 BT", Font.PLAIN, (int)Math.round(UnitConv.pt2mm(80)));
            graphics.setFont(f);
            graphics.drawString('\0'+'\1'+"123 Test Text", 50, 100);
            String[] fontlist = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            for (int i=0; i<fontlist.length; i++) System.out.println(fontlist[i]);
        }
    }

    protected void paintBarcode(Graphics graphics, Rectangle rect) {
        //System.out.println("printBarcode: " + rect);
        final Dimension dim = rect.getSize();
        final Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        CanvasProvider provider = new Java2DCanvasProvider(g2d, 270);
        
        AffineTransform baktrans = g2d.getTransform();
        g2d.translate(rect.getX(), rect.getY());
        BarcodeDimension barDim = bargen.calcDimensions(msg);
        System.out.println("bardim: " + barDim);
        double sc1 = dim.getWidth() / barDim.getWidthPlusQuiet(provider.getOrientation());
        double sc2 = dim.getHeight() / barDim.getHeightPlusQuiet(provider.getOrientation());
        g2d.scale(sc1, sc2);
        
        g2d.setColor(Color.black);
        
        bargen.generateBarcode(provider, msg);
        g2d.setTransform(baktrans);
    }

}
