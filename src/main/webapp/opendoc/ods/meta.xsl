<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0">
    <xsl:output method="xml" indent="yes" encoding="UTF-8" />

    <xsl:param name="schema-url" select="''"/>
    <xsl:param name="table-schema-urls" select="''"/>


    <xsl:template match="@* | node()">
       <xsl:copy>
         <xsl:apply-templates select="@* | node()" />
       </xsl:copy>
    </xsl:template>

        <xsl:template match="meta:user-defined">
            <xsl:choose>
                <xsl:when test="@meta:name='schema-url' and $schema-url!=''">
                    <meta:user-defined meta:name="schema-url"><xsl:value-of select="$schema-url"/></meta:user-defined>
                </xsl:when>
                <xsl:when test="@meta:name='table-schema-urls' and $table-schema-urls!=''">
                    <meta:user-defined meta:name="table-schema-urls"><xsl:value-of select="$table-schema-urls"/></meta:user-defined>
                </xsl:when>
                <xsl:otherwise>
                   <xsl:copy>
                     <xsl:apply-templates select="@* | node()" />
                   </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:template>

</xsl:stylesheet>
