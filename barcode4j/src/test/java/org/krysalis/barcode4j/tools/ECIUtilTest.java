package org.krysalis.barcode4j.tools;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ECIUtilTest {

    @Test
    void getECIForCharset_ISO_8859_1() {
        assertEquals(3, ECIUtil.getECIForCharset(StandardCharsets.ISO_8859_1));
    }

    @Test
    void getECIForCharset_UTF_8() {
        assertEquals(26, ECIUtil.getECIForCharset(StandardCharsets.UTF_8));
    }

    @Test
    void getECIForCharset_US_ASCII() {
        assertEquals(27, ECIUtil.getECIForCharset(StandardCharsets.US_ASCII));
    }

}
