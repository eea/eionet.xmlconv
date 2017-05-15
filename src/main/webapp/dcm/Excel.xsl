<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:table="http://openoffice.org/2000/table" xmlns:text="http://openoffice.org/2000/text">
    <xsl:output  method="xml"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>
    <xsl:param name="dd_schema_url" select="concat($dd_domain,'/GetSchema?id=TBL')"/>

    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>

    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"&#xd;&#xa;xmlns:office='http://openoffice.org/2000/office'&#xd;&#xa; 	xmlns:table='http://openoffice.org/2000/table'&#xd;&#xa; xmlns:text='http://openoffice.org/2000/text'&#xd;&#xa;    xmlns:dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">" xmlns:dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="correspondingNS"/>


        <xsl:text disable-output-escaping="yes">"&gt;&#xd;&#xa;    </xsl:text>




<xsl:text disable-output-escaping="yes">&lt;xsl:template match="dd</xsl:text>
<xsl:value-of select="parentNS"/>
<xsl:text disable-output-escaping="yes">:</xsl:text>
<xsl:value-of select="identifier"/>
<xsl:text disable-output-escaping="yes">"&gt; </xsl:text>
<xsl:text disable-output-escaping="yes">&lt;office:document-content xmlns:office='http://openoffice.org/2000/office' xmlns:table='http://openoffice.org/2000/table' office:version='1.0' xmlns:xlink='http://www.w3.org/1999/xlink' xmlns:number='http://openoffice.org/2000/datastyle' xmlns:text='http://openoffice.org/2000/text' xmlns:fo='http://www.w3.org/1999/XSL/Format' xmlns:style='http://openoffice.org/2000/style'&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;office:automatic-styles&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;style:style style:name='cell1' style:family='table-cell' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;style:properties fo:text-align='left' fo:font-size='10pt'/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/style:style&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;style:style style:name='cell2' style:family='table-cell' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;style:properties fo:text-align='center' fo:font-size='12pt' fo:font-style='italic'/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/style:style&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;style:style style:name='Heading1' style:family='table-cell' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;style:properties fo:text-align='left' fo:font-size='10pt' fo:font-style='italic' style:text-align-source='fix' fo:font-weight='bold'/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/style:style&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;style:style style:name='Heading2' style:family='table-cell' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;style:properties fo:text-align='center' fo:font-size='10pt' fo:font-weight='bold' /&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/style:style&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">	&lt;/office:automatic-styles&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;office:body&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;xsl:attribute name="table:name"&gt;</xsl:text>
<xsl:value-of select="identifier"/>
<xsl:text disable-output-escaping="yes">&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-column table:default-cell-value-type='number' table:default-cell-style-name='cell1' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;xsl:attribute name="table:number-columns-repeated"&gt;&lt;xsl:value-of select="count($elementsMetadata/element)"/&gt;&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-column&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;!-- create header rows --&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;xsl:apply-templates select="dd</xsl:text>
<xsl:value-of select="parentNS"/>
<xsl:text disable-output-escaping="yes">:Row"/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;xsl:call-template name="DD_Schema_sheet"/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/office:body&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/office:document-content&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">&lt;xsl:template match="dd</xsl:text>
<xsl:value-of select="parentNS"/>
<xsl:text disable-output-escaping="yes">:Row"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;xsl:if test="position()=1"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;xsl:call-template name="header"/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/xsl:if&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;table:table-row&gt;</xsl:text>
 <!--   insert data  -->
        <xsl:element name="xsl:variable">
            <xsl:attribute name="name">row</xsl:attribute>
            <xsl:attribute name="select">.</xsl:attribute>
        </xsl:element>
        <xsl:element name="xsl:for-each" namespace="http://www.w3.org/1999/XSL/Transform">
            <xsl:attribute name="select">$elementsMetadata/element</xsl:attribute>
            <xsl:element name="xsl:variable">
                <xsl:attribute name="name">elemIdentifier</xsl:attribute>
                <xsl:element name="xsl:value-of"><xsl:attribute name="select">identifier</xsl:attribute></xsl:element>
            </xsl:element>
            <xsl:element name="xsl:variable">
                <xsl:attribute name="name">multiValueSeparator</xsl:attribute>
                <xsl:element name="xsl:call-template">
                    <xsl:attribute name="name">getSeparator</xsl:attribute>
                    <xsl:element name="xsl:with-param">
                        <xsl:attribute name="name">element</xsl:attribute>
                        <xsl:attribute name="select">$elemIdentifier</xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="table:table-cell">
                <xsl:element name="text:p">
                    <xsl:element name="xsl:choose">
                        <xsl:element name="xsl:when"><xsl:attribute name="test">count($row/*[local-name()= $elemIdentifier])=0 or string-join($row/*[local-name()= $elemIdentifier ],'')=''</xsl:attribute></xsl:element>
                        <xsl:element name="xsl:otherwise"><xsl:element name="xsl:value-of"><xsl:attribute name="select">string-join($row/*[local-name()= $elemIdentifier ],$multiValueSeparator)</xsl:attribute></xsl:element></xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
<xsl:text disable-output-escaping="yes">	&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>


<xsl:text disable-output-escaping="yes">&lt;!--  a named template, which creates the table header row --&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;xsl:template name="header"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;table:table-header-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;table:table-row  table:default-cell-value-type='string' table:default-cell-style-name='Heading2' &gt;</xsl:text>
        <xsl:element name="xsl:for-each" namespace="http://www.w3.org/1999/XSL/Transform">
            <xsl:attribute name="select">$elementsMetadata/element</xsl:attribute>
            <xsl:text disable-output-escaping="yes">				&lt;table:table-cell&gt;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;text:p&gt;&lt;xsl:value-of select="identifier" /&gt;&lt;/text:p&gt; &#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">				&lt;/table:table-cell&gt;</xsl:text>
        </xsl:element>
<xsl:text disable-output-escaping="yes">		&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/table:table-header-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;!-- Creates a sheet with XML schema, as it is done in DataDcit. Then it's possible to convert Excel back to XML--&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;xsl:template name="DD_Schema_sheet"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;xsl:attribute name="table:name"&gt;DO_NOT_DELETE_THIS_SHEET&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-column table:default-cell-value-type='text' table:default-cell-style-name='cell1' /&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">						&lt;text:p&gt;Please do not delete or modify this sheet!!!&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">						&lt;text:p&gt;It is used for converting this file back to XML!&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">						&lt;text:p&gt;Without this possibility your work cannot be used!&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;table:table-cell&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">						&lt;text:p&gt;</xsl:text>
<xsl:value-of select="$dd_schema_url"/>
<xsl:value-of select="tableid"/>
<xsl:text disable-output-escaping="yes">&lt;/text:p&gt;</xsl:text>







<xsl:text disable-output-escaping="yes">					&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>


        <!--   TEMPLATE name="getSeparator"  -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="name">getSeparator</xsl:attribute>
            <xsl:element name="xsl:param"><xsl:attribute name="name">element</xsl:attribute><xsl:attribute name="select">''</xsl:attribute></xsl:element>
            <xsl:choose>
                <xsl:when test="count(elements/element[string-length(multiValueDelim) > 0]) > 0">
                    <xsl:element name="xsl:choose">
                        <xsl:for-each select="elements/element[string-length(multiValueDelim)>0]">
                            <xsl:element name="xsl:when"><xsl:attribute name="test">$element = '<xsl:value-of select="identifier"/>'</xsl:attribute><xsl:value-of select="multiValueDelim"/></xsl:element>
                        </xsl:for-each>
                        <xsl:element name="xsl:otherwise">,</xsl:element>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise><xsl:element name="xsl:value-of"><xsl:attribute name="select">','</xsl:attribute></xsl:element></xsl:otherwise>
            </xsl:choose>
        </xsl:element>

        <!-- DD elements metadata variable -->
        <xsl:element name="xsl:variable" namespace="http://www.w3.org/1999/XSL/Transform">
            <xsl:attribute name="name">elementsMetadata</xsl:attribute>
            <xsl:copy-of select="//elements/element" />
        </xsl:element>

        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>
    </xsl:template>
</xsl:stylesheet>

