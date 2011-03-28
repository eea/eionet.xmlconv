<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:office='http://openoffice.org/2000/office' xmlns:table='http://openoffice.org/2000/table' xmlns:text='http://openoffice.org/2000/text'>

<xsl:template match="measurements">
<office:document-content xmlns:office='http://openoffice.org/2000/office' xmlns:table='http://openoffice.org/2000/table' office:version='1.0' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:number='http://openoffice.org/2000/datastyle' xmlns:text='http://openoffice.org/2000/text' xmlns:fo='http://www.w3.org/1999/XSL/Format' xmlns:style='http://openoffice.org/2000/style'>
    <office:automatic-styles>
        <style:style style:name='cell1' style:family='table-cell' >
            <style:properties fo:text-align='right' fo:font-size='10pt'/>
        </style:style>
        <style:style style:name='cell2' style:family='table-cell' >
            <style:properties fo:text-align='center' fo:font-size='12pt' fo:font-style='italic'/>
        </style:style>
        <style:style style:name='Heading1' style:family='table-cell' >
            <style:properties fo:text-align='left' fo:font-size='10pt' fo:font-style='italic' style:text-align-source='fix' fo:font-weight='bold' fo:font-family='Lucida Console'/>
        </style:style>
        <style:style style:name='Heading2' style:family='table-cell' >
            <style:properties fo:text-align='center' fo:font-size='10pt' fo:font-weight='bold' />
        </style:style>

    </office:automatic-styles>
    <office:body>
        <table:table table:name='measurements'>
            <table:table-columns>
                <table:table-column table:default-cell-value-type='string' table:default-cell-style-name='cell2' />
                <table:table-column table:default-cell-value-type='date' table:default-cell-style-name='cell1' />
                <table:table-column table:number-columns-repeated='18' table:default-cell-value-type='number' table:default-cell-style-name='cell1' />
            </table:table-columns>
            <table:table-header-rows>
                <table:table-row   table:default-cell-value-type='string' table:default-cell-style-name='Heading1' >
                    <table:table-cell>
                        <text:p>Freshwater quality</text:p>
                    </table:table-cell>
                </table:table-row>
                <table:table-row  table:default-cell-value-type='string' table:default-cell-style-name='Heading2' >
                    <table:table-cell>
                        <text:p>Station</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Date</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Q</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Temp</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>pH</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Cond</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>C1</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>NO3N</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>NH4N</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>DO</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>BOD</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>COD</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>PTOT</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Det</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>CD</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>HG</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>ColiFaec</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>ColiTot</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Strep</text:p>
                    </table:table-cell>
                    <table:table-cell>
                        <text:p>Salman</text:p>
                    </table:table-cell>
                </table:table-row>
            </table:table-header-rows>
            <table:table-rows>
                <xsl:apply-templates select="measurement" />
            </table:table-rows>
        </table:table>
    </office:body>
</office:document-content>
</xsl:template>

<xsl:template match="measurement">
    <table:table-row>
        <xsl:apply-templates />
    </table:table-row>
</xsl:template>

<xsl:template match="*">
    <table:table-cell>
        <text:p><xsl:value-of select="@mod" /><xsl:value-of select="." /></text:p>
    </table:table-cell>
</xsl:template>

</xsl:stylesheet>
