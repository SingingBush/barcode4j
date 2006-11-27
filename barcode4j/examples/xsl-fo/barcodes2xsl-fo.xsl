<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:common="http://exslt.org/common" xmlns:xalan="http://xml.apache.org" exclude-result-prefixes="barcode common xalan">
  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ============================================================================================================================= -->
  <xsl:template match="barcodes">
    <fo:root font-family="sans-serif" font-size="10pt">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="1cm" margin-left="2cm" margin-right="2cm">
          <fo:region-body margin-bottom="1.1cm"/>
          <fo:region-after extent="1cm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="simpleA4" language="de_CH">
        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="center" font-style="italic">This PDF was generated using Barcode4J&#160;(<fo:basic-link external-destination="url(http://barcode4j.sourceforge.net)">http://barcode4j.sourceforge.net</fo:basic-link>) and Apache&#160;FOP&#160;(<fo:basic-link external-destination="url(http://xmlgraphics.apache.org/fop/)">http://xmlgraphics.apache.org/fop/</fo:basic-link>)</fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <xsl:apply-templates/>
          <xsl:if test="/barcodes/@add-play-section = 'true'">
            <!-- ======== static section ======= -->
            <xsl:call-template name="section">
              <xsl:with-param name="title" select="'Barcode Rotation'"/>
            </xsl:call-template>
            <xsl:variable name="barcode-cfg">
              <barcode>
                <ean-13/>
              </barcode>
            </xsl:variable>
            <fo:block>A normal barcode:</fo:block>
            <fo:block>
              <fo:instream-foreign-object>
                <xsl:copy-of select="barcode:generate($barcode-cfg, '4006408551379')"/>
              </fo:instream-foreign-object>
            </fo:block>
            <fo:block space-before="2mm">The same barcode, but rotated this time using SVG:</fo:block>
            <fo:block>
              <fo:instream-foreign-object>
                <svg:svg xmlns:svg="http://www.w3.org/2000/svg" width="10cm" height="10cm">
                  <svg:g transform="translate(30, 0), rotate(45)">
                    <xsl:copy-of select="barcode:generate($barcode-cfg, '4006408551379')"/>
                  </svg:g>
                </svg:svg>
              </fo:instream-foreign-object>
            </fo:block>
          </xsl:if>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template match="section">
    <xsl:call-template name="section">
      <xsl:with-param name="title" select="@title"/>
    </xsl:call-template>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template name="section">
    <xsl:param name="title" select="''"/>
    <fo:block break-before="page" font-weight="bold" font-size="16pt" padding-top="1mm" padding-bottom="1mm" background-color="blue" color="white" space-after="3mm" text-align="center">
      <xsl:value-of select="$title"/>
    </fo:block>
  </xsl:template>
  <xsl:template match="barcode">
    <fo:table table-layout="fixed" width="100%" space-before="3mm">
      <fo:table-column column-width="8.5cm"/>
      <fo:table-column column-width="8.5cm"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell padding="1mm" border="solid" border-width="1px">
            <fo:block>
              <xsl:value-of select="description"/>
            </fo:block>
          </fo:table-cell>
          <fo:table-cell background-color="lightgrey" padding="1mm" border="solid" border-width="1px">
              <fo:block>
                <fo:instream-foreign-object>
                  <xsl:variable name="bc" select="barcode:generate(., msg)"/>
                  <svg:svg xmlns:svg="http://www.w3.org/2000/svg">
                    <xsl:attribute name="width"><xsl:value-of select="$bc/svg:svg/@width"/></xsl:attribute>
                    <xsl:attribute name="height"><xsl:value-of select="$bc/svg:svg/@height"/></xsl:attribute>
                    <svg:rect x="0mm" y="0mm" fill="white">
                      <xsl:attribute name="width"><xsl:value-of select="$bc/svg:svg/@width"/></xsl:attribute>
                      <xsl:attribute name="height"><xsl:value-of select="$bc/svg:svg/@height"/></xsl:attribute>
                    </svg:rect>
                    <xsl:copy-of select="$bc"/>
                  </svg:svg>
                  <!--xsl:copy-of select="barcode:generate(., msg)"/-->
                </fo:instream-foreign-object>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:template>
</xsl:stylesheet>
