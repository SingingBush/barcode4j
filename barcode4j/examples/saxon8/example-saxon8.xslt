<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:barcode="java:/org.krysalis.barcode4j.saxon8.BarcodeExtensionElementFactory" extension-element-prefixes="barcode">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
  <xsl:template match="barcode">
    <svg xmlns="http://www.w3.org/2000/svg" width="5cm" height="3.2cm">
      <barcode:barcode message="{.}">
        <barcode:postnet/>
      </barcode:barcode>
      <text x="2.5cm" y="3cm" text-anchor="middle">Barcode4J example: Postnet</text>
    </svg>
  </xsl:template>
</xsl:stylesheet>
