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

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;

/**
 * @version $Id: PlaygroundFrame.java,v 1.3 2004-10-02 14:55:24 jmaerki Exp $
 */
public class PlaygroundFrame extends Frame {

    public static final String TITLE = "Barcode Playground";
    
    public PlaygroundFrame() {
        super(TITLE);
        addWindowListener(new WindowHandler());
        buildGUI();
        setSize(500, 400);
    }
    
    private void buildGUI() {
        BarcodePanel bcpanel = new BarcodePanel();
        add("Center", bcpanel);
        
        try {
            DefaultConfiguration cfg = new DefaultConfiguration("ean-13");
            DefaultConfiguration child = new DefaultConfiguration("human-readable-font");
            //child.setValue("OCR-B 10 Pitch BT");
            child.setValue("Tahoma");
            cfg.addChild(child);
            
            BarcodeGenerator gen = 
                    BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
            
            bcpanel.setBarcode(gen, "419458670510+06");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private class WindowHandler extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }

}
