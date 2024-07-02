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

import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.w3c.dom.DocumentFragment;

import org.krysalis.barcode4j.configuration.Configurable;
import org.krysalis.barcode4j.configuration.Configuration;
import org.krysalis.barcode4j.configuration.ConfigurationException;

/**
 * This is a convenience class to generate barcodes. It is implemented as
 * Singleton to cache the BarcodeClassResolver. However, the class also
 * contains a set of static methods which you can use of you manage your own
 * BarcodeClassResolver.
 *
 * @author Jeremias Maerki
 * @version $Id: BarcodeUtil.java,v 1.6 2012-01-27 14:36:35 jmaerki Exp $
 */
public class BarcodeUtil {

    private final BarcodeClassResolver classResolver = new DefaultBarcodeClassResolver();

    private static class ThreadsafeLazySingleton {
        private static final BarcodeUtil INSTANCE = new BarcodeUtil();
    }

    /**
     * Creates a new BarcodeUtil object. This constructor is private because
     * this class is designed as a singleton.
     */
    private BarcodeUtil() {
        //nop
    }

    /**
     * Returns the default instance of this class.
     * @return the singleton
     */
    public static BarcodeUtil getInstance() {
        return ThreadsafeLazySingleton.INSTANCE;
    }

    /**
     * Returns the class resolver used by this class.
     * @return a BarcodeClassResolver instance
     */
    public BarcodeClassResolver getClassResolver() {
        return this.classResolver;
    }

    /**
     * Creates a BarcoderGenerator.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param classResolver The BarcodeClassResolver to use for lookup of barcode implementations.
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     * @throws ConfigurationException if something's wrong wth the configuration
     */
    public static BarcodeGenerator createBarcodeGenerator(Configuration cfg, BarcodeClassResolver classResolver) throws BarcodeException, ConfigurationException {
        Class<BarcodeGenerator> cl = null;
        try {
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
                throw new BarcodeException("No known barcode configuration element found");
            }

            //Instantiate the BarcodeGenerator
            final BarcodeGenerator gen = cl.newInstance();

            try {
                //org.apache.avalon.framework.container.ContainerUtil.configure(gen, (child != null ? child : cfg));
                configure(gen, (child != null ? child : cfg));
            } catch (IllegalArgumentException iae) {
                throw new ConfigurationException("Cannot configure barcode generator", iae);
            }

            return gen;
        } catch (IllegalAccessException ia) {
            throw new RuntimeException(
                "Problem while instantiating a barcode generator: " + ia.getMessage(),
                ia
            );
        } catch (InstantiationException ie) {
            throw new BarcodeException(
                "Error instantiating a barcode generator: " + cl.getName(),
                ie
            );
        }
    }

    /*
     * Taken from org.apache.avalon.framework.container.ContainerUtil when removing Avalon dependency
     */
    private static void configure( final Object object, final Configuration configuration ) throws ConfigurationException {
        if( object instanceof Configurable) {
            if( null == configuration ) {
                throw new IllegalArgumentException( "configuration is null" );
            }
            ( (Configurable)object ).configure( configuration );
        }
    }

    /**
     * Creates a BarcoderGenerator.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @return the newly instantiated BarcodeGenerator
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     * @throws ConfigurationException if something's wrong wth the configuration
     */
    public BarcodeGenerator createBarcodeGenerator(final Configuration cfg) throws ConfigurationException, BarcodeException {
        return createBarcodeGenerator(cfg, this.classResolver);
    }

    /**
     * Convenience method to create an SVG barocde as a DOM fragment.
     * @param cfg Configuration object that specifies the barcode to produce.
     * @param msg message to encode.
     * @return the requested barcode as an DOM fragment (SVG format)
     * @throws BarcodeException if setting up a BarcodeGenerator fails
     * @throws ConfigurationException if something's wrong wth the configuration
     */
    public DocumentFragment generateSVGBarcode(final Configuration cfg, final String msg) throws ConfigurationException, BarcodeException {
        final BarcodeGenerator gen = createBarcodeGenerator(cfg);
        final SVGCanvasProvider svg = new SVGCanvasProvider(false, 0);

        //Create Barcode and render it to SVG
        gen.generateBarcode(svg, msg);

        return svg.getDOMFragment();
    }

}
