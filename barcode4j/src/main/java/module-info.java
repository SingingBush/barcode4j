/**
 * @author Samael Bate (singingbush)
 * created on 16/10/2025
 */
module barcode4j {

    requires static org.jetbrains.annotations; // only needed for build
    requires static com.google.zxing; // optional in maven

    requires java.desktop;
    requires java.logging;
    requires org.slf4j;

    exports org.krysalis.barcode4j.configuration;
    exports org.krysalis.barcode4j.impl.aztec;
    exports org.krysalis.barcode4j.impl.codabar;
    exports org.krysalis.barcode4j.impl.code39;
    exports org.krysalis.barcode4j.impl.code128;
    exports org.krysalis.barcode4j.impl.datamatrix;
    exports org.krysalis.barcode4j.impl.fourstate;
    exports org.krysalis.barcode4j.impl.int2of5;
    exports org.krysalis.barcode4j.impl.pdf417;
    exports org.krysalis.barcode4j.impl.postnet;
    exports org.krysalis.barcode4j.impl.qr;
    exports org.krysalis.barcode4j.impl.upcean;
    exports org.krysalis.barcode4j.impl;
    exports org.krysalis.barcode4j.output.bitmap;
    exports org.krysalis.barcode4j.output.eps;
    exports org.krysalis.barcode4j.output.java2d;
    exports org.krysalis.barcode4j.output.svg;
    exports org.krysalis.barcode4j.output;
    exports org.krysalis.barcode4j.swing;
    exports org.krysalis.barcode4j.tools;
    exports org.krysalis.barcode4j;
}
