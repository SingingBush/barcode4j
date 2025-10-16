/**
 * @author Samael Bate (singingbush)
 * created on 16/10/2025
 */
module barcode4j.xgc {

    requires static org.jetbrains.annotations; // only needed for build

    requires java.desktop;

    requires barcode4j;

    requires org.apache.xmlgraphics.commons;

    exports org.krysalis.barcode4j.image.loader;
}
