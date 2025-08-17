<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:barcode4j="java:/org.krysalis.barcode4j.BarcodeExtensionElementFactory"
                extension-element-prefixes="barcode4j">
    <xsl:output method="xml" version="3.0" omit-xml-declaration="no" indent="yes"/>
    <!-- ============================================================================================================================= -->
    <xsl:template match="barcodes">
        <results>
            <xsl:apply-templates/>
        </results>
    </xsl:template>
    <xsl:template match="barcode">
        <barcode4j:barcode message="{msg}" orientation="90">
            <barcode4j:code128/>
        </barcode4j:barcode>
    </xsl:template>
</xsl:stylesheet>
