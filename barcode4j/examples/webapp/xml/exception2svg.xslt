<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/2000/svg">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="exception">
	  <svg width="10cm" heigth="5cm">
	    <g style="fill:rgb(191,15,19); font-family:sans-serif">
	      <text x="2mm" y="6mm">
	        <tspan x="2mm" dy="0mm" font-weight="bold">
	          <xsl:value-of select="msg"/>
           </tspan>
           <xsl:apply-templates select="nested"/>
	      </text>
	    </g>
	  </svg>
	</xsl:template>
	<xsl:template match="nested">
	  <tspan x="2mm" dy="5mm">
	    <xsl:value-of select="msg"/>
       <xsl:apply-templates select="nested"/>
	  </tspan>
	</xsl:template>
</xsl:stylesheet>
