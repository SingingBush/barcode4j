<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:barcode="http://barcode4j.krysalis.org/ns">

    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="data">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="page">
                    <fo:region-body
                        region-name="body"
                        margin-top="0.5in"
                        margin-bottom="1in"
                        margin-left="0.5in"
                        margin-right="0.5in"
                    />
                </fo:simple-page-master>
            </fo:layout-master-set>

            <xsl:apply-templates select="barcodes"/>

        </fo:root>
    </xsl:template>

    <xsl:template match="barcodes">
        <fo:page-sequence master-reference="page">
            <fo:flow flow-name="body">
                <fo:block margin-top="2.4cm" margin-left="3cm">
                    <xsl:apply-templates select="barcode" />
                </fo:block>
            </fo:flow>
        </fo:page-sequence>
    </xsl:template>

    <xsl:template match="barcode">
        <xsl:variable name="t" select="type"/>
        <xsl:variable name="msg" select="message"/>

        <fo:block font-size="18pt" margin="0.5cm">
            <xsl:text>Barcode of type </xsl:text>
            <xsl:value-of select="$t"/>
            <xsl:text>.</xsl:text>
        </fo:block>

        <fo:instream-foreign-object>
            <barcode:barcode message="{$msg}">

                <xsl:if test="$t = 'upc-A'">
                    <barcode:upc-A>
                        <barcode:height>15mm</barcode:height>
                    </barcode:upc-A>
                </xsl:if>

                <xsl:if test="$t = 'ean-13'">
                    <barcode:ean-13>
                        <barcode:height>15mm</barcode:height>
                    </barcode:ean-13>
                </xsl:if>

                <xsl:if test="$t = 'code128'">
                    <barcode:code128>
                        <barcode:height>15mm</barcode:height>
                    </barcode:code128>
                </xsl:if>

                <xsl:if test="$t = 'pdf417'">
                    <barcode:pdf417>
                        <barcode:height>15mm</barcode:height>
                    </barcode:pdf417>
                </xsl:if>

            </barcode:barcode>
        </fo:instream-foreign-object>
    </xsl:template>

</xsl:stylesheet>
