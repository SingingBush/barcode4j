/**
 * @author Samael Bate (singingbush)
 * created on 16/10/2025
 */
module barcode4j.fop {

    requires static org.jetbrains.annotations; // only needed for build

    requires java.desktop;

    requires barcode4j;

    requires org.apache.xmlgraphics.fop.core;
    requires org.apache.xmlgraphics.commons;
    requires org.apache.xmlgraphics.batik.anim;

    exports org.krysalis.barcode4j.fop;
}
