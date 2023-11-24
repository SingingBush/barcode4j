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
                <fo:block>
                    <xsl:apply-templates select="barcode" />
                </fo:block>
            </fo:flow>
        </fo:page-sequence>
    </xsl:template>

    <xsl:template match="barcode">
        <xsl:variable name="t" select="type"/>
        <xsl:variable name="msg" select="message"/>

        <fo:block font-size="18pt" margin="0.4cm">
            <xsl:text>Barcode of type </xsl:text>
            <xsl:value-of select="$t"/>
            <xsl:text>:</xsl:text>
        </fo:block>

        <fo:block text-align="center" padding-bottom="0.8cm" border-bottom="1pt solid blue">
            <fo:instream-foreign-object>
                <barcode:barcode message="{$msg}">

                    <xsl:if test="$t = 'upc-A'">
                        <barcode:upc-A>
                            <barcode:height>15mm</barcode:height>
                            <barcode:module-width>0.33mm</barcode:module-width>
                            <barcode:quiet-zone enabled="true">10mw</barcode:quiet-zone>
                            <barcode:checksum>auto</barcode:checksum>
                        </barcode:upc-A>
                    </xsl:if>

                    <xsl:if test="$t = 'ean-13'">
                        <barcode:ean-13>
                            <barcode:height>15mm</barcode:height>
                            <barcode:module-width>0.33mm</barcode:module-width>
                            <barcode:quiet-zone enabled="true">10mw</barcode:quiet-zone>
                            <barcode:checksum>auto</barcode:checksum>
                        </barcode:ean-13>
                    </xsl:if>

                    <xsl:if test="$t = 'code128'">
                        <barcode:code128>
                            <barcode:height>15mm</barcode:height>
                            <barcode:module-width>0.21mm</barcode:module-width>
                            <barcode:codesets>ABC</barcode:codesets>
                            <barcode:quiet-zone enabled="true">10mw</barcode:quiet-zone>
                        </barcode:code128>
                    </xsl:if>

                    <xsl:if test="$t = 'pdf417'">
                        <barcode:pdf417>
                            <barcode:module-width>0.705554mm</barcode:module-width> <!-- 2 pixels at 72dpi -->
                            <barcode:row-height>3mw</barcode:row-height>
                            <barcode:columns>2</barcode:columns>
                            <barcode:min-columns>2</barcode:min-columns>
                            <barcode:max-columns>2</barcode:max-columns>
                            <barcode:min-rows>3</barcode:min-rows>
                            <barcode:max-rows>90</barcode:max-rows>
                            <barcode:ec-level>0</barcode:ec-level>
                            <barcode:quiet-zone enabled="true">2mw</barcode:quiet-zone>
    <!--                        <barcode:vertical-quiet-zone>{length:default is same as quiet-zone}</barcode:vertical-quiet-zone>-->
                            <barcode:width-to-height-ratio>3.0</barcode:width-to-height-ratio>
                        </barcode:pdf417>
                    </xsl:if>

                    <xsl:if test="$t = 'datamatrix'">
                        <barcode:datamatrix>
                            <barcode:module-width>1.411108mm</barcode:module-width> <!-- 4 pixels at 72dpi -->
                            <barcode:quiet-zone enabled="true">1mw</barcode:quiet-zone>
                            <barcode:shape>force-none</barcode:shape>
                            <barcode:min-symbol-size>22</barcode:min-symbol-size>
                            <barcode:max-symbol-size>26</barcode:max-symbol-size>
                        </barcode:datamatrix>
                    </xsl:if>

                </barcode:barcode>
            </fo:instream-foreign-object>
        </fo:block>
    </xsl:template>

</xsl:stylesheet>
