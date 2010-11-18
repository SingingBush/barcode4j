/*
 * Copyright 2005,2010 Jeremias Maerki.
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
package org.krysalis.barcode4j.fop;

import java.awt.geom.Point2D;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.tools.ConfigurationUtil;
import org.krysalis.barcode4j.tools.MessageUtil;
import org.krysalis.barcode4j.tools.PageInfo;
import org.krysalis.barcode4j.tools.UnitConv;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.fop.apps.FOPException;
import org.apache.fop.fo.FONode;
import org.apache.fop.fo.PropertyList;

/**
 * Class representing bc:barcode extension element object.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeElement.java,v 1.8 2010-11-18 09:30:45 jmaerki Exp $
 */
public class BarcodeElement extends BarcodeObj {

    /** @see org.apache.fop.fo.FONode#FONode(FONode) */
    public BarcodeElement(FONode parent) {
        super(parent);
    }

    /**
     * @see org.apache.fop.fo.FONode#processNode
     */
    public void processNode(String elementName,
                            Locator locator,
                            Attributes attlist,
                            PropertyList propertyList) throws FOPException {
        super.processNode(elementName, locator, attlist, propertyList);
        init();
    }

    private void init() {
        createBasicDocument();
    }

    public Point2D getDimension(Point2D view) {
        Configuration cfg = ConfigurationUtil.buildConfiguration(this.doc);
        try {
            String msg = ConfigurationUtil.getMessage(cfg);
            msg = MessageUtil.unescapeUnicode(msg);

            int orientation = cfg.getAttributeAsInteger("orientation", 0);
            orientation = BarcodeDimension.normalizeOrientation(orientation);

            BarcodeGenerator bargen = BarcodeUtil.getInstance().
                    createBarcodeGenerator(cfg);
            String expandedMsg = VariableUtil.getExpandedMessage((PageInfo)null, msg);
            BarcodeDimension bardim = bargen.calcDimensions(expandedMsg);
            float w = (float)UnitConv.mm2pt(bardim.getWidthPlusQuiet(orientation));
            float h = (float)UnitConv.mm2pt(bardim.getHeightPlusQuiet(orientation));
            return new Point2D.Float(w, h);
        } catch (ConfigurationException ce) {
            ce.printStackTrace();
        } catch (BarcodeException be) {
            be.printStackTrace();
        }
        return null;
    }


}
