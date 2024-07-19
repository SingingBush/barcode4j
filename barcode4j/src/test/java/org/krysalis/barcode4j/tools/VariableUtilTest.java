package org.krysalis.barcode4j.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class VariableUtilTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "#page-number#",
        //"#page-number:", // todo: take a look at how this should be used
        "#formatted-page-number#"
    })
    @DisplayName("Should expand page number or default to 000 if no PageInfo")
    void getExpandedMessage(final String message) {
        assertEquals("1", VariableUtil.getExpandedMessage(new PageInfo(1, "1"), message));
        assertEquals("25", VariableUtil.getExpandedMessage(new PageInfo(25, "25"), message));

        // A null page info results in "000"
        assertEquals("000", VariableUtil.getExpandedMessage(null, message));
    }

    @Test
    @DisplayName("Should use correct number format")
    void getExpandedMessage_number_format() {
        assertEquals("3", VariableUtil.getExpandedMessage(new PageInfo(3, "III"), "#page-number#"));
        assertEquals("III", VariableUtil.getExpandedMessage(new PageInfo(3, "III"), "#formatted-page-number#"));
    }

    @Test
    @DisplayName("Should handle null message")
    void getExpandedMessage_null() {
        assertNull(VariableUtil.getExpandedMessage(null, null));

        assertNull(VariableUtil.getExpandedMessage(new PageInfo(1, "1"), null));
    }
}
