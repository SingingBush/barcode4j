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
package org.krysalis.barcode4j;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.DocumentFragment;

/**
 * This is a convenience class to generate barcodes. It is implemented as
 * Singleton to cache the BarcodeClassResolver. However, the class also
 * contains a set of static methods which you can use of you manage your own
 * BarcodeClassResolver.
 * 
 * @author Jeremias Maerki
 * @version $Id: BarcodeUtil.java,v 1.2 2004-09-04 20:25:54 jmaerki Exp $
 */
public class BarcodeUtil {
    
    private static BarcodeUtil instance = null;
    
    private BarcodeClassResolver classResolver = new DefaultBarcodeClassResolver();
    
    
    /**
     * Creates a new BarcodeUtil object. This constructor is protected because
     * this class is designed as a singleton.
     */
    protected BarcodeUtil() {
        //nop
    }
    
    /**
     * Returns the default instance of this class.
     * @return the singleton
     */
    public static BarcodeUtil getInstance() {
        if (instance == null) {
            instance = new BarcodeUtil();
        }
        return instance;
    }
    
    /**
     * Creates a BarcoderGenerator.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param logger The logger to use
     * @param classResolver The BarcodeClassResolver to use for lookup of
     * barcode implementations.
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     */
    public static BarcodeGenerator createBarcodeGenerator(Configuration cfg, 
                                    Logger logger,
                                    BarcodeClassResolver classResolver) 
            throws BarcodeException {
        try {
            Class cl = null;
            
            //First, check Configuration directly
            String type = cfg.getName();
            try {
                cl = classResolver.resolve(type);
            } catch (ClassNotFoundException cnfe) {
                cl = null;
            }
            Configuration child = null;
            if (cl == null) {
                //Second, check children
                Configuration[] children = cfg.getChildren();
                if (children.length == 0) {
                    throw new BarcodeException("Barcode configuration element expected");
                }

                //Find barcode config element            
                for (int i = 0; i < children.length; i++) {
                    child = children[i];
                    type = child.getName();
                    try {
                        cl = classResolver.resolve(type);
                        break;
                    } catch (ClassNotFoundException cnfe) {
                        cl = null;
                    }
                }
            }

            if (cl == null) {
                throw new BarcodeException(
                    "No barcode configuration element not found");
            }

            //Instantiate the BarcodeGenerator            
            BarcodeGenerator gen = (BarcodeGenerator)cl.newInstance();
            if (gen instanceof LogEnabled) {
                ((LogEnabled)gen).enableLogging(logger);
            }
            if (gen instanceof Configurable) {
                ((Configurable)gen).configure((child != null ? child : cfg));
            }
            if (gen instanceof Initializable) {
                ((Initializable)gen).initialize();
            }
            return gen;
        } catch (Exception e) {
            throw new BarcodeException("Error instantiating a barcode generator", e);
        }
    }
            
    /**
     * Creates a BarcoderGenerator.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param logger The logger to use
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     */
    public BarcodeGenerator createBarcodeGenerator(Configuration cfg,
                                                   Logger logger) 
            throws BarcodeException {
        return createBarcodeGenerator(cfg, logger, this.classResolver);
    }
    
    /**
     * Convenience method to create an SVG barocde as a DOM fragment.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param logger The logger to use
     * @param msg message to encode.
     * @return the requested barcode as an DOM fragment (SVG format)
     * @throws BarcodeException if an error occurs during barcode generation
     */
    public DocumentFragment generateBarcode(Configuration cfg,
                                            Logger logger,
                                            String msg) 
                    throws BarcodeException {
        BarcodeGenerator gen = createBarcodeGenerator(cfg, logger);
        try {
            SVGCanvasProvider svg = new SVGCanvasProvider(false);

            //Create Barcode and render it to SVG
            gen.generateBarcode(svg, msg);
    
            return svg.getDOMFragment();
        } catch (Exception e) {
            throw new BarcodeException("Error while generating barcode", e);
        }
    }

}
