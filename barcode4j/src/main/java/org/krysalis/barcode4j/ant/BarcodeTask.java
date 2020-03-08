/*
 * Copyright 2008 Jeremias Maerki.
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
package org.krysalis.barcode4j.ant;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for Barcode4J.
 *
 * @author Didier Donsez
 */
public class BarcodeTask extends Task {

    /** The barcode symbology to select */
    private String symbol;

    /** The configuration file */
    private File configurationFile;

    /** (for bitmaps) the image resolution in dpi */
    private int dpi = 300;

    /**
     * The output format: MIME type or file extension
     */
    private String format = MimeTypes.MIME_SVG;

    /** the output filename */
    private File output;

    /** (for bitmaps) create monochrome (1-bit) image instead of grayscale (8-bit) */
    private boolean bw = true;

    /** the message */
    private String message;

    /**
     * Handles the command line. The method calls the exit handler upon
     * completion.
     * @throws BuildException if an error occurs
     */
    public void execute() throws BuildException {

        if (message == null || message.length() == 0) {
            throw new BuildException("No message");
        }

        if (output == null) {
            throw new BuildException("Output file is missing");
        }

        try {
            OutputStream out = new java.io.FileOutputStream(output);

            format = MimeTypes.expandFormat(format);

            int orientation = 0;
            log("Generating " + symbol + " in " + format + "...");
            BarcodeUtil util = BarcodeUtil.getInstance();
            BarcodeGenerator gen = util.createBarcodeGenerator(getConfiguration());

            if (MimeTypes.MIME_SVG.equals(format)) {
                // Create Barcode and render it to SVG
                SVGCanvasProvider svg = new SVGCanvasProvider(false, orientation);
                gen.generateBarcode(svg, message);

                // Serialize SVG barcode
                try {
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(svg.getDOMFragment());
                    Result res = new javax.xml.transform.stream.StreamResult(out);
                    trans.transform(src, res);
                } catch (TransformerException te) {
                    throw new BuildException("XML/XSLT library error", te);
                }
            } else if (MimeTypes.MIME_EPS.equals(format)) {
                EPSCanvasProvider eps = new EPSCanvasProvider(out, orientation);
                gen.generateBarcode(eps, message);
                eps.finish();
            } else {
                BitmapCanvasProvider bitmap;
                if (bw) {
                    bitmap = new BitmapCanvasProvider(out, format, dpi,
                            BufferedImage.TYPE_BYTE_BINARY, false, orientation);
                } else {
                    bitmap = new BitmapCanvasProvider(out, format, dpi,
                            BufferedImage.TYPE_BYTE_GRAY, true, orientation);
                }
                gen.generateBarcode(bitmap, message);
                bitmap.finish();
            }

            out.close();
        } catch (IOException ioe) {
            throw new BuildException("Error writing output file: " + ioe.getMessage());
        } catch (ConfigurationException ce) {
            throw new BuildException("Configuration problem: " + ce.getMessage(), ce);
        } catch (BarcodeException be) {
            throw new BuildException("Error generating the barcode", be);
        }
    }

    private Configuration getConfiguration() {
        if (symbol != null) {
            DefaultConfiguration cfg = new DefaultConfiguration("cfg");
            DefaultConfiguration child = new DefaultConfiguration(symbol);
            cfg.addChild(child);
            return cfg;
        }
        if (configurationFile != null) {
            try {
                if (!configurationFile.exists() || !configurationFile.isFile()) {
                    throw new BuildException("Config file not found: " + configurationFile);
                }
                log("Using configuration: " + configurationFile);

                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                return builder.buildFromFile(configurationFile);
            } catch (Exception e) {
                throw new BuildException("Error reading configuration file: " + e.getMessage());
            }
        }
        return new DefaultConfiguration("cfg");
    }

    /**
     * Sets the desired symbology.
     * @param symbol the symbology to set
     * @see http://barcode4j.sourceforge.net/latest/barcode-xml.html
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Sets the configuration file to use for complex configuration.
     * @param configurationFile the configurationFile to set
     */
    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * Sets the resolution (used when creating bitmap output).
     * @param dpi the dpi to set
     */
    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    /**
     * Sets the desired output format.
     * <p>
     * A selection of valid output formats:
     * <ul>
     * <li>SVG: image/svg+xml, svg</li>
     * <li>EPS: image/x-eps, eps</li>
     * <li>PNG: image/x-png, png</li>
     * <li>TIFF: image/tiff, tiff, tif</li>
     * <li>JPEG: image/jpeg, jpeg, jpg</li>
     * <li>GIF: image/gif, gif</li>
     * </ul>
     * @param format the format (a MIME type or a supported format identifier)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the target file.
     * @param output the output
     */
    public void setOutput(File output) {
        this.output = output;
    }

    /**
     * Controls whether to generate monochrome (black and white, 1 bit) images. Only applicable
     * when generating bitmap formats.
     * @param bw true for monochrome images
     */
    public void setBw(boolean bw) {
        this.bw = bw;
    }

    /**
     * Sets the barcode message.
     * @param message the message
     */
    public void setMessage(String message) {
        if (this.message != null) {
            this.message += message;
        } else {
            this.message = message;
        }
    }

    /**
     * Adds text to the current message.
     * @param text the text to add to the message
     */
    public void addText(String text) {
        if (this.message != null) {
            this.message += text;
        } else {
            this.message = text;
        }
    }
}
