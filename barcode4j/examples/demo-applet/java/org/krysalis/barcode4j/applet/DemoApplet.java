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

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Demo Applet class
 * 
 * @author Jeremias Maerki
 */
public class DemoApplet extends Applet 
        implements BarcodeModelListener, ActionListener, DocumentListener,
            BarcodeErrorListener {

    private static final String[] BARCODE_TYPES = 
        {"codabar", "code39", "code128", "2of5", "postnet", 
            "ean-13", "ean-8", "upc-a", "upc-e"};

    //Controller part
    private Model model = new Model();
    
    //View part
    private AbstractBarcode barcode;
    private JComboBox symbology;
    private JTextField msgField;
    
    public void initComponents() {
        msgField = new JTextField();
        symbology = new JComboBox(BARCODE_TYPES);
    }

    public JPanel buildControlPanel() {
        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        GridBagAdder.add(panel, symbology, 0, 0, 4, 1, 0, 0, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        GridBagAdder.add(panel, msgField, 0, 1, 4, 1, 1, 0, 
                GridBagConstraints.BOTH, GridBagConstraints.CENTER);
        panel.setBackground(Color.lightGray);
        return panel;
    }

    public void init() {
        setBackground(Color.white);
        setLayout(new BorderLayout());

        initComponents();

        model.addChangeListener(this);

        msgField.setDocument(new PlainDocument());
        try {
            msgField.getDocument().insertString(0, "123456", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        msgField.getDocument().addDocumentListener(this);
        symbology.addActionListener(this);
        
        barcode = new Barcode();
        barcode.setBarcodeGenerator(getModel().getBean());
        barcode.addErrorListener(this);
        
        add(BorderLayout.CENTER, barcode);
        add(BorderLayout.SOUTH, buildControlPanel());

        updateModel();
        valueChanged();
    }
    
    public Model getModel() {
        return this.model;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == symbology) {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                updateModel();
            }
        }
    }

    public void changedUpdate(DocumentEvent e) {
        updateModel();
    }

    public void insertUpdate(DocumentEvent e) {
        updateModel();
    }

    public void removeUpdate(DocumentEvent e) {
        updateModel();
    }

    public void notifySuccess() {
        barcode.setToolTipText(null);
    }
    
    public void notifyException(Exception e) {
        System.out.println("notifyException: " + e);
        barcode.setToolTipText(e.getMessage());
        //TODO Maybe manually show tooltip (didn't find a way to do that, yet)
    }

    public void valueChanged() {
        //System.out.println("valueChanged()");
        barcode.setBarcodeGenerator(getModel().getBean());
        barcode.setMessage(getModel().getMessage());
    }
    
    private void updateModel() {
        //System.out.println("updateModel()");
        getModel().setup(symbology.getSelectedItem().toString());
        try {
            getModel().setMessage(msgField.getDocument().getText(
                        0, msgField.getDocument().getLength()));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    static class GridBagAdder {
        private static GridBagConstraints cons = new GridBagConstraints();
        
        public static void add(Container cont, Component comp, 
                int x, int y, 
                int width, int height, 
                int weightx, int weighty, 
                int fill, int anchor) {
            cons.gridx = x;
            cons.gridy = y;
            cons.gridwidth = width;
            cons.gridheight = height;
            cons.weightx = weightx;
            cons.weighty = weighty;
            cons.fill = fill;
            cons.anchor = anchor;
            cont.add(comp, cons);
        }
    }

}
