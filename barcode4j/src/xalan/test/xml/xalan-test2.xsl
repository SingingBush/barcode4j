<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bc="org.krysalis.barcode4j.xalan.BarcodeExt" 
      extension-element-prefixes="bc">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ============================================================================================================================= -->
  <xsl:template match="barcodes">
    <results>
      <xsl:apply-templates/>
    </results>
  </xsl:template>
  <xsl:template match="barcode">
    <bc:barcode message="{msg}">
      <bc:code128>
        <bc:module-width>2mm<!--a comment for testing--></bc:module-width>
      </bc:code128>
    </bc:barcode>
  </xsl:template>
</xsl:stylesheet>