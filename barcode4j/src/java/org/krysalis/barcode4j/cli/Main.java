/*
 * $Id: Main.java,v 1.1 2003-12-13 20:23:42 jmaerki Exp $
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
package org.krysalis.barcode4j.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.bitmap.BitmapEncoderRegistry;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * Command-line interface.
 * 
 * @author Jeremias Maerki
 */
public class Main {

    private static final String[] APP_HEADER = {
        "Barcode4J command-line application, Version 1.0beta",
        "Copyright (c) 2000-2003 "
                + "The Krysalis Community Project. All rights reserved.",
        ""};

    /** stdout for this application (default: System.out) */
    public static PrintStream stdout = System.out;
    /** stderr for this application (default: System.err) */
    public static PrintStream stderr = System.err;
    
    private static ExitHandler exitHandler = new DefaultExitHandler(); 
    private Options options;
    private boolean headerPrinted = false;
    private Logger log;

    /**
     * Main method.
     * @param args commandline arguments
     */
    public static void main(String[] args) {
        Main app = new Main();
        app.handleCommandLine(args);
        /*
        try {
            app.handleCommandLine(args);
        } catch (Exception e) {
            exitHandler.failureExit(app, "Unhandled exception", e, -1);
        }*/
    }
    
    /**
     * Set an alternative exit handler here.
     * @param handler the alternative exit handler
     */
    public static void setExitHandler(ExitHandler handler) {
        exitHandler = handler;
    }
    
    /**
     * Handles the command line. The method calls the exit handler upon 
     * completion.
     * @param args the command line arguments
     */
    public void handleCommandLine(String[] args) {
        CommandLine cl;
        String[] msg;
        try {
            CommandLineParser clp = new PosixParser();
            cl = clp.parse(getOptions(), args);
            
            //Message
            msg = cl.getArgs();
            if (msg.length == 0) {
                throw new ParseException("No message");
            }
            if (msg.length > 1) {
                throw new ParseException("Too many parameters: " + msg.length);
            }
        } catch (MissingOptionException moe) {
            printHelp(new PrintWriter(stdout));
            exitHandler.failureExit(this, 
                "Bad command line. Missing option: " + moe.getMessage(), null, -2);
            return; //never reached
        } catch (ParseException pe) {
            printHelp(new PrintWriter(stdout));
            //pe.printStackTrace();
            exitHandler.failureExit(this, 
                "Bad command line: " + pe.getMessage(), null, -2);
            return; //never reached
        }
        try {
            OutputStream out;
            if (!cl.hasOption("o")) {
                log = new AdvancedConsoleLogger(AdvancedConsoleLogger.LEVEL_ERROR,
                    false, stderr, stderr);
                printAppHeader();
                out = stdout;
            } else {
                int logLevel = AdvancedConsoleLogger.LEVEL_INFO;
                if (cl.hasOption('v')) {
                    logLevel = AdvancedConsoleLogger.LEVEL_DEBUG;
                }
                log = new AdvancedConsoleLogger(logLevel, false, stdout, stderr);
                printAppHeader();
                File outFile = new File(cl.getOptionValue("o"));
                if (log.isDebugEnabled()) {
                    log.debug("Output to: " + outFile.getCanonicalPath());
                } 
                out = new java.io.FileOutputStream(outFile);
            }
            
            log.debug("Message: " + msg[0]);
            
            //Output format
            String format = MimeTypes.expandFormat(
                    cl.getOptionValue("f", MimeTypes.MIME_SVG));
            log.info("Generating " + format + "...");
            BarcodeUtil util = BarcodeUtil.getInstance();
            BarcodeGenerator gen = util.createBarcodeGenerator(
                    getConfiguration(cl), log);
            
            if (MimeTypes.MIME_SVG.equals(format)) {
                //Create Barcode and render it to SVG
                SVGCanvasProvider svg = new SVGCanvasProvider(false);
                gen.generateBarcode(svg, msg[0]);
    
                //Serialize SVG barcode
                try {
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(
                        svg.getDOMFragment());
                    Result res = new javax.xml.transform.stream.StreamResult(out);
                    trans.transform(src, res);
                } catch (TransformerException te) {
                    exitHandler.failureExit(this, "XML/XSLT library error", te, -6);
                }
            } else if (MimeTypes.MIME_EPS.equals(format)) {
                EPSCanvasProvider eps = new EPSCanvasProvider(out);
                gen.generateBarcode(eps, msg[0]);
                eps.finish();
            } else {
                int dpi = Integer.parseInt(cl.getOptionValue('d', "300"));
                log.debug("Resolution: " + dpi + "dpi");
                BitmapCanvasProvider bitmap;
                if (cl.hasOption("bw")) {
                    log.debug("Black/white image (1-bit)");
                    bitmap = new BitmapCanvasProvider(out, 
                        format, dpi, BufferedImage.TYPE_BYTE_BINARY, false);
                } else {
                    log.debug("Grayscale image (8-bit) with anti-aliasing");
                    bitmap = new BitmapCanvasProvider(out, 
                        format, dpi, BufferedImage.TYPE_BYTE_GRAY, true);
                }
                gen.generateBarcode(bitmap, msg[0]);
                bitmap.finish();
            }
            
            out.close();
            log.info("done.");
            exitHandler.successfulExit(this);
        } catch (IOException ioe) {
            exitHandler.failureExit(this, 
                "Error writing output file: " + ioe.getMessage(), null, -5);
        } catch (BarcodeException be) {
            exitHandler.failureExit(this, 
                "Error generating the barcode", be, -3);
        }
    }
    
    private Options getOptions() {
        if (options == null) {
            this.options = new Options();
            Option opt;
            
            this.options.addOption(OptionBuilder
                .withLongOpt("verbose")
                .withDescription("enable debug output")
                .create('v'));
                
            //Group: file/stdout
            this.options.addOption(OptionBuilder
                .withLongOpt("output")
                .withArgName("file")
                .hasArg()
                .withDescription("the output filename")
                .create('o'));
            
            //Group: config file/barcode type
            OptionGroup group = new OptionGroup();
            group.setRequired(true);
            group.addOption(OptionBuilder
                .withArgName("file")
                .withLongOpt("config")
                .hasArg()
                .withDescription("the config file")
                .create('c'));
            group.addOption(OptionBuilder
                .withArgName("name")
                .withLongOpt("symbol")
                .hasArg()
                .withDescription("the barcode symbology to select "
                    + "(default settings, use -c if you want to customize)")
                .create('s'));
            this.options.addOptionGroup(group);

            //Output format type
            this.options.addOption(OptionBuilder
                .withArgName("format")
                .withLongOpt("format")
                .hasArg()
                .withDescription("the output format: MIME type or file "
                    + "extension\n"
                    + "Default: " + MimeTypes.MIME_SVG + " (SVG)")
                .create('f'));

            //Bitmap-specific options
            this.options.addOption(OptionBuilder
                .withArgName("integer")
                .withLongOpt("dpi")
                .hasArg()
                .withDescription("(for bitmaps) the image resolution in dpi\n"
                    + "Default: 300")
                .create('d'));
            this.options.addOption(OptionBuilder
                .withLongOpt("bw")
                .withDescription("(for bitmaps) create monochrome (1-bit) "
                    + "image instead of grayscale (8-bit)")
                .create());
        }
        return this.options;
    }

    private Configuration getConfiguration(CommandLine cl) {
        if (cl.hasOption("s")) {
            String sym = cl.getOptionValue("s");
            DefaultConfiguration cfg = new DefaultConfiguration("cfg");
            DefaultConfiguration child = new DefaultConfiguration(sym);
            cfg.addChild(child);
            return cfg;
        } 
        if (cl.hasOption("c")) {
            try {
                String filename = cl.getOptionValue("c");
                File cfgFile = new File(filename);
                if (!cfgFile.exists() || !cfgFile.isFile()) {
                    throw new FileNotFoundException(
                        "Config file not found: " + cfgFile);
                }
                log.info("Using configuration: " + cfgFile);
                
                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                return builder.buildFromFile(cfgFile);
            } catch (Exception e) {
                exitHandler.failureExit(this, 
                    "Error reading configuration file: " + e.getMessage(), null, -3);
            }
        }
        return new DefaultConfiguration("cfg");
    }

    /**
     * Prints the application header on the console. Ensures that this is only
     * done once.
     */
    public void printAppHeader() {
        if (!headerPrinted) {
            if (log != null) {
                for (int i = 0; i < APP_HEADER.length; i++) {
                    log.info(APP_HEADER[i]);
                }
            } else {
                for (int i = 0; i < APP_HEADER.length; i++) {
                    stdout.println(APP_HEADER[i]);
                }
            }
            headerPrinted = true;
        }
    }
    
    private void printHelp(PrintWriter writer) {
        printAppHeader();

        //Get a list of additional supported MIME types
        Set knownMimes = new java.util.HashSet();
        knownMimes.add(null);
        knownMimes.add("");
        knownMimes.add(MimeTypes.MIME_PNG);
        knownMimes.add("image/png");
        knownMimes.add(MimeTypes.MIME_JPEG);
        knownMimes.add(MimeTypes.MIME_TIFF);
        knownMimes.add(MimeTypes.MIME_GIF);
        Set additionalMimes = BitmapEncoderRegistry.getSupportedMIMETypes();
        additionalMimes.removeAll(knownMimes);

        HelpFormatter help = new HelpFormatter();
        final String unavailable = " (unavailable)";
        help.printHelp(writer, HelpFormatter.DEFAULT_WIDTH,
            "java -jar barcode4j.jar "
                + "[-v] [[-s <symbology>]|[-c <cfg-file>]] [-f <format>] "
                + "[-d <dpi>] [-bw] [-o <file>] <message>",
            null,
            getOptions(),
            HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD,
            "Valid output formats:"
                + "\nSVG: " + MimeTypes.MIME_SVG + ", svg"
                + "\nEPS: " + MimeTypes.MIME_EPS + ", eps"
                + "\nPNG: " + MimeTypes.MIME_PNG + ", png" 
                    + (BitmapEncoderRegistry.supports(MimeTypes.MIME_PNG) 
                        ? "" : unavailable)
                + "\nTIFF: " + MimeTypes.MIME_TIFF + ", tiff, tif"
                    + (BitmapEncoderRegistry.supports(MimeTypes.MIME_TIFF) 
                        ? "" : unavailable)
                + "\nJPEG: " + MimeTypes.MIME_JPEG + ", jpeg, jpg"
                    + (BitmapEncoderRegistry.supports(MimeTypes.MIME_JPEG) 
                        ? "" : unavailable)
                + "\nGIF: " + MimeTypes.MIME_GIF + ", gif"
                    + (BitmapEncoderRegistry.supports(MimeTypes.MIME_GIF) 
                        ? "" : unavailable)
                + (additionalMimes.size() > 0 
                    ? "\nAdditional supported formats:\n" + additionalMimes 
                    : "")
                + "\n"
                + "\nIf -o is omitted the output is written to stdout.");
        writer.flush();
        
    }

}