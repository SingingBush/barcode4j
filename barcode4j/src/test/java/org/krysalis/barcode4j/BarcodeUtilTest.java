package org.krysalis.barcode4j;

import org.junit.jupiter.api.Test;
import org.krysalis.barcode4j.configuration.ConfigurationException;
import org.krysalis.barcode4j.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.impl.int2of5.Interleaved2Of5;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Samael Bate (singingbush)
 * created on 03/08/2024
 */
class BarcodeUtilTest {

    @Test
    void testCreateBarcodeGenerator() throws ConfigurationException, BarcodeException {
        final DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("intl2of5"));

        final BarcodeGenerator barcodeGenerator = BarcodeUtil.createBarcodeGenerator(
            cfg,
            new DefaultBarcodeClassResolver()
        );

        assertEquals(Interleaved2Of5.class, barcodeGenerator.getClass());
    }

    @Test
    void testCreateBarcodeGeneratorWithInvalidConfig() {
        assertThrows(BarcodeException.class, () -> BarcodeUtil.createBarcodeGenerator(
            new DefaultConfiguration(""),
            new DefaultBarcodeClassResolver()
        ));
    }

    @Test
    void testCreateBarcodeGeneratorWithInvalidSymbology() {
        final DefaultConfiguration cfg = new DefaultConfiguration("cfg");
        cfg.addChild(new DefaultConfiguration("badsymbol"));

        assertThrows(BarcodeException.class, () -> BarcodeUtil.createBarcodeGenerator(
            cfg,
            new DefaultBarcodeClassResolver()
        ));
    }
}
