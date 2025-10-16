/**
 * @author Samael Bate (singingbush)
 * created on 16/10/2025
 */
module barcode4j.xalan {

    requires static org.jetbrains.annotations; // only needed for build

    requires java.xml;

    requires barcode4j;

    requires xalan;

    exports org.krysalis.barcode4j.xalan;
}
