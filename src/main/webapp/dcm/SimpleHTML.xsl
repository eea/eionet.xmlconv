<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output  method="xml" indent="yes"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>

    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>

    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"&#xd;&#xa;    xmlns:dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">" xmlns:dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="correspondingNS"/>
        <xsl:text disable-output-escaping="yes">"&gt;&#xd;&#xa;    &lt;xsl:output method="html"/&gt;&#xd;&#xa;</xsl:text>

  <!--   TEMPLATE match ="/"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">    &lt;xsl:template match="/"&gt;&#xd;&#xa;</xsl:text>


        <xsl:text disable-output-escaping="yes">&lt;html&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;head&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;title&gt;</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">&lt;/title&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/head&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;body&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;h1&gt;</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">&lt;/h1&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;table border="1" style="border-collapse:collapse;"&gt;</xsl:text>


        <xsl:text disable-output-escaping="yes">        &lt;xsl:apply-templates select="dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">/dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:Row"/&gt;&#xd;&#xa;</xsl:text>

        <xsl:text disable-output-escaping="yes">&lt;/table&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/body&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/html&gt;</xsl:text>



        <xsl:text disable-output-escaping="yes">    &lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>

<!--   TEMPLATE match ="Row"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">    &lt;xsl:template match="dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">/dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:Row"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;xsl:if test="position()=1"&gt;&#xd;&#xa;</xsl:text>



        <xsl:text disable-output-escaping="yes">            &lt;xsl:call-template name="header"/&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>

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
              <xsl:element name="td">
                   <xsl:element name="xsl:choose">
                        <xsl:element name="xsl:when"><xsl:attribute name="test">count($row/*[local-name()= $elemIdentifier])=0 or string-join($row/*[local-name()= $elemIdentifier ],'')=''</xsl:attribute></xsl:element>
                         <xsl:element name="xsl:otherwise"><xsl:element name="xsl:value-of"><xsl:attribute name="select">string-join($row/*[local-name()= $elemIdentifier ],$multiValueSeparator)</xsl:attribute></xsl:element></xsl:element>
                   </xsl:element>
              </xsl:element>
         </xsl:element>
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>



        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>

   <!--   TEMPLATE name="header"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="header"&gt;&#xd;&#xa;</xsl:text>

        <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
         <xsl:element name="xsl:for-each" namespace="http://www.w3.org/1999/XSL/Transform">
              <xsl:attribute name="select">$elementsMetadata/element</xsl:attribute>
               <xsl:text disable-output-escaping="yes">&lt;th bgcolor="#87cefa"&gt;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="identifier" /&gt; &#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;/th&gt; &#xd;&#xa;</xsl:text>
        </xsl:element>
        <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>

        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>


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

