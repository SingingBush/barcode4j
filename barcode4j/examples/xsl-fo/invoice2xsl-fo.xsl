<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:barcode="org.krysalis.barcode4j.xalan.BarcodeExt" xmlns:date="http://exslt.org/dates-and-times" extension-element-prefixes="barcode date">
  <xsl:output method="xml" indent="yes"/>
  <xsl:decimal-format name="std" decimal-separator="." grouping-separator="'" infinity="#x221E" minus-sign="-"/>
  <xsl:template match="/">
    <fo:root>
      <!-- defines the layout master -->
      <fo:layout-master-set>
        <fo:simple-page-master master-name="normal_page" page-height="29.7cm" page-width="21cm" margin-top="3cm" margin-bottom="2.5cm" margin-left="3cm" margin-right="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <!-- starts actual layout -->
      <fo:page-sequence master-reference="normal_page">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-family="Helvetica" font-size="11pt" font-weight="normal">
            <fo:table table-layout="fixed" space-after="2cm">
              <fo:table-column column-width="8cm"/>
              <fo:table-column column-width="8cm"/>
              <fo:table-body>
                <fo:table-row>
                  <fo:table-cell text-align="start">
                    <fo:block>
                      <fo:external-graphic src="url(http://barcode4j.krysalis.org/images/group-logo.gif)" width="4cm"/>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell text-align="end">
                    <fo:block>
                      <fo:external-graphic src="url(http://barcode4j.krysalis.org/resources/images/barcode4j-logo.gif)" width="3.5cm"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
            <fo:table table-layout="fixed" space-after="1cm">
              <fo:table-column column-width="4cm"/>
              <fo:table-column column-width="5cm"/>
              <fo:table-column column-width="6.5cm"/>
              <fo:table-body>
                <fo:table-row height="3.0cm">
                  <!-- should have: height="3.0cm" -->
                  <fo:table-cell padding-top="1cm"/>
                  <fo:table-cell/>
                  <fo:table-cell padding-top="1cm">
                    <xsl:apply-templates select="/invoice/header/client"/>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row height="1.5cm"/>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block>Invoice date:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block><xsl:value-of select="date:format-date(/invoice/header/invoicedate, 'EEE, d MMM yyyy')"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block>For questions about this invoice:</fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block>Invoice number:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block><xsl:value-of select="/invoice/header/invoicenr"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block>Hotline:<xsl:text> 555 5555</xsl:text>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell>
                    <fo:block>Client number:</fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block><xsl:value-of select="/invoice/header/client/id"/></fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block>e-mail:<xsl:text> hotline@shop.demo</xsl:text></fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                  <fo:table-cell/>
                  <fo:table-cell>
                    <fo:block>
                      <fo:instream-foreign-object>
                        <!-- ================================================================= -->
                        <!-- ====================== THE BARCODE IS HERE ====================== -->
                        <!-- ================================================================= -->
                        <xsl:variable name="barcode-cfg">
                          <barcode>
                            <code128>
                              <human-readable>none</human-readable>
                              <height>5mm</height>
                              <quiet-zone enabled="false"/>
                            </code128>
                          </barcode>
                        </xsl:variable>
                        <xsl:copy-of select="barcode:generate($barcode-cfg, /invoice/header/invoicenr)"/>
                        <!-- ================================================================= -->
                        <!-- ====================== THE BARCODE IS HERE ====================== -->
                        <!-- ================================================================= -->
                      </fo:instream-foreign-object>
                    </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                    <fo:block>Page<xsl:text>&#x20;</xsl:text>
                      <fo:page-number/>
                      <xsl:text>&#x20;</xsl:text>of<xsl:text>&#x20;</xsl:text>
                      <fo:page-number-citation ref-id="endofdoc"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-body>
            </fo:table>
            <xsl:apply-templates select="/invoice/details"/>
            <fo:block id="endofdoc"/>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template match="details">
    <fo:table table-layout="fixed" space-before="2mm">
      <fo:table-column column-width="13cm"/>
      <fo:table-column column-width="3cm"/>
      <fo:table-header padding-bottom="1mm">
        <fo:table-row border-bottom-style="solid" border-bottom-width="0.5pt">
          <fo:table-cell border-bottom="solid 0.5pt">
            <fo:block font-weight="bold">Article</fo:block>
          </fo:table-cell>
          <fo:table-cell border-bottom="solid 0.5pt">
            <fo:block font-weight="bold" text-align="end">Amount</fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-header>
      <fo:table-body>
        <xsl:apply-templates/>
        <fo:table-row height="1mm"/>
        <fo:table-row border-top-style="solid" border-top-width="0.5pt" border-bottom-style="solid" border-bottom-width="1.5pt">
          <fo:table-cell border-top="solid 0.5pt" border-bottom="solid 1.5pt" padding-top="1mm">
            <fo:block font-weight="bold">Total</fo:block>
          </fo:table-cell>
          <fo:table-cell border-top="solid 0.5pt" border-bottom="solid 1.5pt" padding-top="1mm">
            <fo:block font-weight="bold" text-align="end">
              <xsl:value-of select="format-number(sum(position/@amount), '0.00', 'std')"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </xsl:template>
  <xsl:template match="position">
    <fo:table-row>
      <fo:table-cell>
        <fo:block>
          <xsl:value-of select="."/>
        </fo:block>
      </fo:table-cell>
      <xsl:if test="@count != ''">
        <fo:table-cell>
          <fo:block text-align="end">
            <xsl:value-of select="@count"/>
          </fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block text-align="end">
            <xsl:value-of select="format-number(number(@unitprice), '0.00', 'std')"/>
          </fo:block>
        </fo:table-cell>
      </xsl:if>
      <fo:table-cell>
        <fo:block text-align="end">
          <xsl:value-of select="format-number(number(@amount), '0.00', 'std')"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>
  <!-- ===== adress formatting ===== -->
  <xsl:template match="client">
    <xsl:call-template name="address-private-salutation"/>
  </xsl:template>
  <xsl:template name="address-private-salutation">
    <fo:block>
      <xsl:value-of select="@salutation"/>
    </fo:block>
    <xsl:call-template name="address-private"/>
  </xsl:template>
  <xsl:template name="address-private">
    <fo:block>
      <xsl:call-template name="name-private-with-title"/>
    </fo:block>
    <xsl:apply-templates select="postal"/>
  </xsl:template>
  <xsl:template name="name-private-with-title">
    <xsl:if test="@title!=''">
      <xsl:value-of select="@title"/>
      <xsl:text>&#x20;</xsl:text>
    </xsl:if>
    <xsl:call-template name="name-private"/>
  </xsl:template>
  <xsl:template name="name-private">
    <xsl:value-of select="firstname"/>
    <xsl:text>&#x20;</xsl:text>
    <xsl:value-of select="name"/>
  </xsl:template>
  <xsl:template match="postal">
    <fo:block>
      <xsl:value-of select="street"/>
    </fo:block>
    <fo:block>
      <xsl:value-of select="pobox"/>
    </fo:block>
    <fo:block>
      <xsl:value-of select="zip/@countrycode"/>
      <xsl:text>-</xsl:text>
      <xsl:value-of select="zip"/>
      <xsl:text>&#x20;</xsl:text>
      <xsl:value-of select="place"/>
    </fo:block>
  </xsl:template>
</xsl:stylesheet>
