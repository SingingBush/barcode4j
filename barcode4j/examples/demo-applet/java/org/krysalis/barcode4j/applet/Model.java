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

import java.util.Iterator;
import java.util.List;

import org.krysalis.barcode4j.BarcodeClassResolver;
import org.krysalis.barcode4j.DefaultBarcodeClassResolver;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;

/**
 * Model class for the applet.
 * 
 * @author Jeremias Maerki
 */
public class Model {

    private String msg;
    private String lastType = "none";
    private AbstractBarcodeBean bean;
    private BarcodeClassResolver resolver = new DefaultBarcodeClassResolver();
    private List changeListeners = new java.util.LinkedList();
    
    public Model() {
        this.msg = "";
        setup("codabar");
    }

    public void addChangeListener(BarcodeModelListener listener) {
        changeListeners.add(listener);
    }
    
    public void fireValueChanged() {
        Iterator i = changeListeners.iterator();
        while (i.hasNext()) {
            ((BarcodeModelListener)i.next()).valueChanged();
        }
    }
    
    public String getMessage() {
        return this.msg;
    }
    
    public void setMessage(String msg) {
        if (msg == null) {
            throw new NullPointerException("Parameter msg must not be null");
        }
        msg = EscapeHandler.parseEscapes(msg);
        if (!this.msg.equals(msg)) {
            this.msg = msg;
            fireValueChanged();
        }
    }
    
    public AbstractBarcodeBean getBean() {
        return this.bean;
    }
    
    public void setup(String type) {
        if (type == null) {
            throw new NullPointerException("Parameter type must not be null");
        }
        if (!lastType.equals(type)) {
            Class clazz;
            try {
                clazz = resolver.resolveBean(type);
                this.bean = (AbstractBarcodeBean)clazz.newInstance();
                this.bean.doQuietZone(true);
            } catch (Exception e) {
                this.bean = null;
                e.printStackTrace();
            }
            fireValueChanged();
            lastType = type;
        }
    }
}
