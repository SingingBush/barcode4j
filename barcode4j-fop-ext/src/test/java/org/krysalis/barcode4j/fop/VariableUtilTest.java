package org.krysalis.barcode4j.fop;

import org.apache.fop.area.PageViewport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VariableUtilTest {

    @Test
    @DisplayName("Should handle null values correctly (especially page string)")
    void getExpandedMessage() {
        final PageViewport pageViewport = mock(PageViewport.class);
        when(pageViewport.getPageNumber()).thenReturn(1);
        when(pageViewport.getPageNumberString()).thenReturn(null);

        assertNull(VariableUtil.getExpandedMessage((PageViewport)null, null));
        assertNull(VariableUtil.getExpandedMessage(pageViewport, null));

        assertEquals("Hello", VariableUtil.getExpandedMessage((PageViewport)null, "Hello"));
        assertEquals("Hello", VariableUtil.getExpandedMessage(pageViewport, "Hello"));

        assertEquals("1", VariableUtil.getExpandedMessage(pageViewport, "#page-number#"));
        assertEquals("1", VariableUtil.getExpandedMessage(pageViewport, "#formatted-page-number#"));

        when(pageViewport.getPageNumberString()).thenReturn("A");
        assertEquals("1", VariableUtil.getExpandedMessage(pageViewport, "#page-number#"));
        assertEquals("A", VariableUtil.getExpandedMessage(pageViewport, "#formatted-page-number#"));
    }
}
