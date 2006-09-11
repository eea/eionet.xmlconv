<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output  method="xml"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>
    <xsl:param name="dd_schema_url" select="concat($dd_domain,'/GetSchema?id=TBL')"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>
    
    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"&#xd;&#xa;xmlns:office='urn:oasis:names:tc:opendocument:xmlns:office:1.0'&#xd;&#xa; xmlns:table='urn:oasis:names:tc:opendocument:xmlns:table:1.0'&#xd;&#xa; xmlns:text='urn:oasis:names:tc:opendocument:xmlns:text:1.0'&#xd;&#xa;    xmlns:dd</xsl:text>
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
<xsl:text disable-output-escaping="yes">&lt;office:document-content xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:ooo="http://openoffice.org/2004/office" xmlns:ooow="http://openoffice.org/2004/writer" xmlns:oooc="http://openoffice.org/2004/calc" xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" office:version="1.0"&gt;</xsl:text>
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
<xsl:text disable-output-escaping="yes">	&lt;office:spreadsheet&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;xsl:attribute name="table:name"&gt;</xsl:text>
<xsl:value-of select="identifier"/>
<xsl:text disable-output-escaping="yes">&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;xsl:attribute name="schema-url"&gt;<xsl:value-of select="$dd_schema_url"/><xsl:value-of select="tableid"/>&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;xsl:attribute name="table-schema-urls"&gt;<xsl:value-of select="$dd_schema_url"/><xsl:value-of select="tableid"/>&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-column table:default-cell-value-type='number' table:default-cell-style-name='cell1' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;xsl:attribute name="table:number-columns-repeated"&gt;&lt;xsl:value-of select="count(./dd</xsl:text>
<xsl:value-of select="parentNS"/>
<xsl:text disable-output-escaping="yes">:Row[1]/*)"/&gt;&lt;/xsl:attribute&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-column&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-columns&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;!-- create header rows --&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;xsl:apply-templates select="dd</xsl:text>
<xsl:value-of select="parentNS"/>
<xsl:text disable-output-escaping="yes">:Row"/&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/table:table&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/office:spreadsheet&gt;</xsl:text>
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
<xsl:text disable-output-escaping="yes">		&lt;xsl:apply-templates /&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">&lt;!--   template for building table cells with values --&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">&lt;xsl:template match="*"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;text:p&gt;&lt;xsl:value-of select="." /&gt;&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>

<xsl:text disable-output-escaping="yes">&lt;!--  a named template, which creates the table header row --&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;xsl:template name="header"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;table:table-header-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;!--table:table-row   table:default-cell-value-type='string' table:default-cell-style-name='Heading1' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;text:p&gt;Groundwater Body Characteristics and Pressures&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/table:table-row--&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;table:table-row  table:default-cell-value-type='string' table:default-cell-style-name='Heading2' &gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;xsl:for-each select="*"&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">					&lt;text:p&gt;&lt;xsl:value-of select="local-name()" /&gt;&lt;/text:p&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">				&lt;/table:table-cell&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">			&lt;/xsl:for-each&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">		&lt;/table:table-row&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">	&lt;/table:table-header-rows&gt;</xsl:text>
<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;</xsl:text>


        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>       
        
    </xsl:template> 
    
</xsl:stylesheet>

