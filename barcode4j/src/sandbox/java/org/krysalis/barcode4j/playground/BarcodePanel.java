/*
 * $Id: BarcodePanel.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
 * ============================================================================
 * The Krysalis Patchy Software License, Version 1.1_01
 * Copyright (c) 2002-2003 Nicola Ken Barozzi.  All rights reserved.
 *
 * This Licence is compatible with the BSD licence as described and
 * approved by http://www.opensource.org/, and is based on the
 * Apache Software Licence Version 1.1.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed for project
 *        Krysalis (http://www.krysalis.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Krysalis" and "Nicola Ken Barozzi" and
 *    "Barcode4J" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact nicolaken@krysalis.org.
 *
 * 5. Products derived from this software may not be called "Krysalis"
 *    or "Barcode4J", nor may "Krysalis" appear in their name,
 *    without prior written permission of Nicola Ken Barozzi.
 *
 * 6. This software may contain voluntary contributions made by many
 *    individuals, who decided to donate the code to this project in
 *    respect of this licence, and was originally created by
 *    Jeremias Maerki <jeremias@maerki.org>.
 *
 * THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE KRYSALIS PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

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

        Font f = new Font("Tahoma", Font.PLAIN, (int)Math.round(UnitConv.pt2mm(80)));
        graphics.setFont(f);
        graphics.drawString("123 Test Text", 50, 50);

        f = new Font("OCR-B-10 BT", Font.PLAIN, (int)Math.round(UnitConv.pt2mm(80)));
        graphics.setFont(f);
        graphics.drawString('\0'+'\1'+"123 Test Text", 50, 100);
        String[] fontlist = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i=0; i<fontlist.length; i++) System.out.println(fontlist[i]);
    }

    protected void paintBarcode(Graphics graphics, Rectangle rect) {
        //System.out.println("printBarcode: " + rect);
        final Dimension dim = rect.getSize();
        final Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        AffineTransform baktrans = g2d.getTransform();
        g2d.translate(rect.getX(), rect.getY());
        BarcodeDimension barDim = bargen.calcDimensions(msg);
        //System.out.println("bardim: " + barDim);
        g2d.scale(dim.getWidth() / barDim.getWidthPlusQuiet(), 
                  dim.getHeight() / barDim.getHeightPlusQuiet());
        
        g2d.setColor(Color.black);
        
        bargen.generateBarcode(new Java2DCanvasProvider(g2d), msg);
        g2d.setTransform(baktrans);
    }

}
