<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- generate HTML skeleton on root element -->
<xsl:template match="/">
    <html>
        <head>
            <title>Eper values for Ireland</title>
        </head>
        <body>
            <xsl:apply-templates/>
        </body>
    </html>
</xsl:template>
<xsl:template match="report">
    <h1>
        <xsl:apply-templates select="SubmissionDate"/><br/>
        <xsl:apply-templates select="Country"/><br/>
        <xsl:apply-templates select="ReportYear"/><br/></h1><br/><b>Measurements:</b><br/><xsl:apply-templates select="Facility"/></xsl:template><xsl:template match="Facility"><table border="0" width="500"><tr><td><table><tr><td width="100"></td><td width="400"><xsl:apply-templates select="ParentCompanyName"/></td></tr></table></td></tr><tr>
                            <td>
                                <table>
                                <tr>
                                <td width="150"></td>
                                <td width="350">
                                    <xsl:apply-templates select="Emission"/>
                                </td>
                                </tr>
                                </table>

                            </td>
                        </tr>
                    </table>
        </xsl:template>

        <xsl:template match="Emission">
                <xsl:value-of select="PollutantName"/> : <xsl:value-of select="EmissionValue"/>
        </xsl:template>

        <xsl:template match="SubmissionDate">
        Submission Date: <xsl:value-of select="."/>
        </xsl:template>

        <xsl:template match="ReportYear">
             <font size="4" color="green"> Year: <xsl:value-of select="."/></font>
    </xsl:template>
        <!-- convert sections to XSL-FO headings -->
    <xsl:template match="Country">
            <font size="6" color="red"> Country: <xsl:value-of select="."/></font>
    </xsl:template>

</xsl:stylesheet>
