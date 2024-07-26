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

import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;

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

    /**
     * @param parent XSL-FO node of the parent
     * @see org.apache.fop.fo.FONode#FONode(FONode)
     */
    public BarcodeElement(FONode parent) {
        super(parent);
    }

    /**
     * @param elementName element name (e.g., "fo:block")
     * @param locator Locator object (ignored by default)
     * @param attlist Collection of attributes passed to us from the parser.
     * @param propertyList the property list of the parent node
     * @throws FOPException if Apache FOP cannot process node
     * @see org.apache.fop.fo.FONode#processNode(String, Locator, Attributes, PropertyList)
     */
    @Override
    public void processNode(String elementName,
                            Locator locator,
                            Attributes attlist,
                            PropertyList propertyList) throws FOPException {
        super.processNode(elementName, locator, attlist, propertyList);
        createBasicDocument();
    }

    /**
     * @param view Point2D instance to receive the dimensions
     * @return dimensions of the generated barcode
     * @see org.apache.fop.fo.XMLObj#getDimension(Point2D)
     */
    @Override
    public Point2D getDimension(Point2D view) {
        final Configuration cfg = ConfigurationUtil.buildConfiguration(this.doc); // consider moving this to just after call to createBasicDocument()
        try {
            final String msg = MessageUtil.unescapeUnicode(ConfigurationUtil.getMessage(cfg));

            final int orientation = BarcodeDimension.normalizeOrientation(cfg.getAttributeAsInteger("orientation", 0));

            final BarcodeGenerator barcodeGen = BarcodeUtil.getInstance().createBarcodeGenerator(cfg);
            final String expandedMsg = VariableUtil.getExpandedMessage((PageInfo)null, msg);
            final BarcodeDimension barcodeDim = barcodeGen.calcDimensions(expandedMsg);
            final float w = (float)UnitConv.mm2pt(barcodeDim.getWidthPlusQuiet(orientation));
            final float h = (float)UnitConv.mm2pt(barcodeDim.getHeightPlusQuiet(orientation));
            return new Point2D.Float(w, h);
        } catch (final ConfigurationException | BarcodeException e) {
            e.printStackTrace(); // todo: use slf4j-api to log error
        }
        return null;
    }

}
