<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:barcode4j="http://barcode4j.krysalis.org/org.krysalis.barcode4j.saxon9.BarcodeExtensionElementFactory"
                extension-element-prefixes="barcode4j">
    <xsl:output method="html" indent="yes" />

    <!-- ============================================================================================================================= -->
    <xsl:template match="barcodes">
        <div class="results">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="barcode">

        <div class="result">
        <xsl:text>Barcode of type </xsl:text>
        <strong>
            <xsl:value-of select="type"/>
        </strong>
        <xsl:text>:</xsl:text>

        <xsl:if test="type = 'upc-A'">
            <barcode4j:barcode message="{message}">
                <barcode4j:upc-A>
                    <barcode4j:height>15mm</barcode4j:height>
                    <barcode4j:module-width>0.33mm</barcode4j:module-width>
                    <barcode4j:quiet-zone enabled="true">10mw</barcode4j:quiet-zone>
                    <barcode4j:checksum>auto</barcode4j:checksum>
                </barcode4j:upc-A>
            </barcode4j:barcode>
        </xsl:if>

        <xsl:if test="type = 'ean-13'">
            <barcode4j:barcode message="{message}">
                <barcode4j:ean-13>
                    <barcode4j:height>15mm</barcode4j:height>
                    <barcode4j:module-width>0.33mm</barcode4j:module-width>
                    <barcode4j:quiet-zone enabled="true">10mw</barcode4j:quiet-zone>
                    <barcode4j:checksum>auto</barcode4j:checksum>
                </barcode4j:ean-13>
            </barcode4j:barcode>
        </xsl:if>

        <xsl:if test="type = 'code128'">
            <barcode4j:barcode message="{message}">
                <barcode4j:code128>
                    <barcode4j:height>15mm</barcode4j:height>
                    <barcode4j:module-width>0.21mm</barcode4j:module-width>
                    <barcode4j:codesets>ABC</barcode4j:codesets>
                    <barcode4j:quiet-zone enabled="true">10mw</barcode4j:quiet-zone>
                </barcode4j:code128>
            </barcode4j:barcode>
        </xsl:if>

        <xsl:if test="type = 'pdf417'">
            <barcode4j:barcode message="{message}">
                <barcode4j:pdf417>
                    <barcode4j:module-width>0.705554mm</barcode4j:module-width> <!-- 2 pixels at 72dpi -->
                    <barcode4j:row-height>3mw</barcode4j:row-height>
                    <barcode4j:columns>2</barcode4j:columns>
                    <barcode4j:min-columns>2</barcode4j:min-columns>
                    <barcode4j:max-columns>2</barcode4j:max-columns>
                    <barcode4j:min-rows>3</barcode4j:min-rows>
                    <barcode4j:max-rows>90</barcode4j:max-rows>
                    <barcode4j:ec-level>0</barcode4j:ec-level>
                    <barcode4j:quiet-zone enabled="true">2mw</barcode4j:quiet-zone>
                    <!--<barcode4j:vertical-quiet-zone>{length:default is same as quiet-zone}</barcode4j:vertical-quiet-zone>-->
                    <barcode4j:width-to-height-ratio>3.0</barcode4j:width-to-height-ratio>
                </barcode4j:pdf417>
            </barcode4j:barcode>
        </xsl:if>

        <xsl:if test="type = 'datamatrix'">
            <barcode4j:barcode message="{message}">
                <barcode4j:datamatrix>
                    <barcode4j:module-width>1.411108mm</barcode4j:module-width> <!-- 4 pixels at 72dpi -->
                    <barcode4j:quiet-zone enabled="true">1mw</barcode4j:quiet-zone>
                    <barcode4j:shape>force-none</barcode4j:shape>
                    <barcode4j:min-symbol-size>22</barcode4j:min-symbol-size>
                    <barcode4j:max-symbol-size>26</barcode4j:max-symbol-size>
                </barcode4j:datamatrix>
            </barcode4j:barcode>
        </xsl:if>

        </div>
    </xsl:template>

    <xsl:template match="/">
        <html lang="en">
            <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <title>XML to HTML</title>
                <style>
                    .result {
                      padding: 0.6em 0.4em;
                      margin-bottom: 0.4em;
                      border-bottom: 1px solid blue;
                    }
                </style>
            </head>
            <body>
                <div>
                    <xsl:apply-templates />
                </div>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
