<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bc="java:/org.krysalis.barcode4j.saxon8.BarcodeExtensionElementFactory"
 	extension-element-prefixes="bc">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ============================================================================================================================= -->
  <xsl:template match="barcodes">
    <results>
      <xsl:apply-templates/>
    </results>
  </xsl:template>
  <xsl:template match="barcode">
      <bc:barcode message="{msg}" orientation="90">
        <bc:code128/>
      </bc:barcode>
  </xsl:template>
</xsl:stylesheet>
