<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match="measurements">
    <html>
        <head>
            <title>Average pH values</title>
        </head>
        <body>
            <h2>Average yearly pH value per station</h2>
            <table border="1">
                <tr>
                    <td></td>
                    <td><b>1998</b></td>
                    <td><b>1999</b></td>
                    <td><b>2000</b></td>
                    <td><b>2001</b></td>
                </tr>
                <tr>
                    <td><b>D 1</b></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 1' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 1' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 1' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 1' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 1' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 1' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 1' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 1' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                </tr>
                <tr>
                    <td><b>D 2</b></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 2' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 2' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 2' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 2' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 2' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 2' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 2' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 2' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                </tr>
                <tr>
                    <td><b>D 3</b></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 3' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 3' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 3' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 3' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 3' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 3' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 3' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 3' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                </tr>
                <tr>
                    <td><b>D 4</b></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 4' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 4' and starts-with(Date, '1998')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 4' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 4' and starts-with(Date, '1999')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 4' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 4' and starts-with(Date, '2000')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                    <td><xsl:value-of select="substring(sum(measurement[Stnr='D 4' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']) div
                                                         count(measurement[Stnr='D 4' and starts-with(Date, '2001')]/sample[@method='pH' and .!='']), 1, 5)"/></td>
                </tr>
            </table>
        </body>
    </html>
</xsl:template>
</xsl:stylesheet>
