package org.krysalis.barcode4j.xalan;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.xalan.templates.ElemEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ElemWrappingConfigurationTest {

    private ElemWrappingConfiguration configuration;

    @Mock
    private Element mockElement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        configuration = new ElemWrappingConfiguration(mockElement);
    }

    @Test
    void getPrefixShouldNotInteractWithElement() throws ConfigurationException {
        assertNull(configuration.getPrefix());
        verifyNoInteractions(mockElement);
    }

    @Test
    void getNameShouldGetNameFromElement() {
        assertNull(configuration.getName());
        verify(mockElement, times(1)).getLocalName();
    }

    @Test
    void getLocationShouldNotInteractWithElement() {
        assertEquals("unknown", configuration.getLocation());
        verifyNoInteractions(mockElement);
    }

    @Test
    void getNamespaceShouldNotInteractWithElement() throws ConfigurationException {
        assertNull(configuration.getNamespace());
        verifyNoInteractions(mockElement);
    }

    @Test
    void getChildren() {
        final NodeList nodeList = mock(NodeList.class);
        when(nodeList.getLength()).thenReturn(1);
        final Node node = new ElemEmpty();
        when(nodeList.item(0)).thenReturn(node);

        when(mockElement.getChildNodes()).thenReturn(nodeList);

        final Configuration[] children = configuration.getChildren();
        assertEquals(1, children.length);
        assertTrue(ElemWrappingConfiguration.class.isAssignableFrom(children[0].getClass()));
    }

    @Test
    void getAttributeNames() {
        assertThrows(UnsupportedOperationException.class, () -> {
            configuration.getAttributeNames();
        });
        verifyNoInteractions(mockElement);
    }

    @Test
    void getAttribute() throws ConfigurationException {
        final String expectedText = "The Result";
        when(mockElement.getAttribute(eq("some text"))).thenReturn(expectedText);

        final String result = configuration.getAttribute("some text");

        assertEquals(expectedText, result);
    }
}
