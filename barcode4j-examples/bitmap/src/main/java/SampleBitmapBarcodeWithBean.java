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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;

/**
 * This example demonstrates creating a bitmap barcode using the bean API.
 *
 * @author Jeremias Maerki
 * @version $Id: SampleBitmapBarcodeWithBean.java,v 1.2 2006-11-07 16:45:28 jmaerki Exp $
 */
public class SampleBitmapBarcodeWithBean {

    private static final int DPI = 300; // for print, 300 dpi is pretty good
    private static final int ORIENTATION = 0;

    public static void main(String[] args) {
        final Code39Bean bean = new Code39Bean();

        // Configure the barcode generator:
        bean.setHeight(15.0);
        //bean.setBarHeight(10.0);
        //bean.setModuleWidth(UnitConv.in2mm(1.0f / DPI));
        bean.setWideFactor(3.0);
        bean.doQuietZone(true);

        try {
            try (final OutputStream out = Files.newOutputStream(new File("out.jpg").toPath())) {
                // Set up the canvas provider for monochrome JPEG output
                final BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    out, MimeTypes.MIME_JPEG, DPI, BufferedImage.TYPE_BYTE_BINARY, false, ORIENTATION);

                // Set up the canvas provider for colour JPEG output
                //final BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                //    out, MimeTypes.MIME_JPEG, DPI, BufferedImage.TYPE_INT_RGB, false, ORIENTATION);

                //Generate the barcode
                bean.generateBarcode(canvas, "123456");

                //Signal end of generation
                canvas.finish();
            }
        } catch (final IOException e) {
            System.err.println(e.getMessage()); // e.printStackTrace();
        }
    }
}
