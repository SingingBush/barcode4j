<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:bc="http://barcode4j.krysalis.org/org.krysalis.barcode4j.saxon.BarcodeExtensionElementFactory" xmlns:saxon="http://icl.com/saxon"
 	extension-element-prefixes="saxon bc">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ============================================================================================================================= -->
  <xsl:template match="barcodes">
    <results>
      <xsl:apply-templates/>
    </results>
  </xsl:template>
  <xsl:template match="barcode">
      <bc:barcode message="{msg}">
        <bc:code128/>
      </bc:barcode>
  </xsl:template>
</xsl:stylesheet>
