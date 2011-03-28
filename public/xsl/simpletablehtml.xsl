<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="measurements">
    <html>
        <head>
            <title>Freshwater quality</title>
        </head>
        <body>
            <h2>Freshwater quality</h2>
            <table border="1">
<tr>
<th>Stnr</th>
<th>Date</th>
<th>Q</th>
<th>Temp</th>
<th>pH</th>
<th>Cond</th>
<th>Cl</th>
<th>NO3N</th>
<th>NH4N</th>
<th>DO</th>
<th>BOD</th>
<th>COD</th>
<th>PTOT</th>
<th>Det</th>
<th>CD</th>
<th>HG</th>
<th>ColiFaec</th>
<th>ColiTot</th>
<th>Strep</th>
<th>Salman</th>
</tr>
                <xsl:apply-templates select="measurement" />
            </table>
        </body>
    </html>
</xsl:template>

<xsl:template match="measurement">
    <tr>
        <xsl:apply-templates />
    </tr>
</xsl:template>

<xsl:template match="Stnr">
    <th><xsl:value-of select="." /></th>
</xsl:template>

<xsl:template match="*">
    <td><xsl:value-of select="@mod" /><xsl:value-of select="." /></td>
</xsl:template>

</xsl:stylesheet>
