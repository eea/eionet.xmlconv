<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output  method="xml"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>
    
    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"&#xd;&#xa;  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"&#xd;&#xa;  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"&#xd;&#xa;</xsl:text>

<!-- DD namespaces  -->
         <xsl:text disable-output-escaping="yes">  xmlns:dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">"&#xd;&#xa;  xmlns:dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="correspondingNS"/>
		<xsl:text disable-output-escaping="yes">"&#xd;&#xa;</xsl:text>
		<xsl:text disable-output-escaping="yes">  exclude-result-prefixes="dd</xsl:text>
        <xsl:value-of select="parentNS"/>
   		<xsl:text disable-output-escaping="yes"> dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
		<xsl:text disable-output-escaping="yes">"&#xd;&#xa;</xsl:text>

        <xsl:text disable-output-escaping="yes">  xmlns:elem="</xsl:text>
        <xsl:value-of select="$dd_domain"/>
        <xsl:text disable-output-escaping="yes">/tables/</xsl:text>
        <xsl:value-of select="tableid"/>
        <xsl:text disable-output-escaping="yes">/rdf#"&gt;&#xd;&#xa; </xsl:text>
        <xsl:text disable-output-escaping="yes">   &lt;xsl:output method="xml"/&gt;&#xd;&#xa;</xsl:text>
 
  <!--   TEMPLATE match ="/"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">    &lt;xsl:template match="/"&gt;&#xd;&#xa;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">      &lt;rdf:RDF&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;xsl:apply-templates select="dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">/dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:Row"/&gt;&#xd;&#xa;</xsl:text>
       
        <xsl:text disable-output-escaping="yes">      &lt;/rdf:RDF&gt;&#xd;&#xa;</xsl:text> 
        
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
        <xsl:text disable-output-escaping="yes">      &lt;rdf:Description&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;xsl:attribute name="rdf:ID"&gt;&lt;xsl:value-of select="generate-id()" /&gt;&lt;/xsl:attribute&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;rdf:type rdf:resource="</xsl:text>
        <xsl:value-of select="$dd_domain"/>
        <xsl:text disable-output-escaping="yes">/tables/</xsl:text>
        <xsl:value-of select="tableid"/>
        <xsl:text disable-output-escaping="yes">/rdf/</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">"/&gt; &lt;!-- known by DD --&gt;&#xd;&#xa;</xsl:text>

		<xsl:text disable-output-escaping="yes">        &lt;xsl:for-each select="*"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">          &lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;elem:</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;&lt;xsl:value-of select="local-name()" /&gt;&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">            &lt;xsl:value-of select="." /&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">          &lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;/elem:</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;&lt;xsl:value-of select="local-name()" /&gt;&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>

        <xsl:text disable-output-escaping="yes">      &lt;/rdf:Description&gt;&#xd;&#xa;</xsl:text>

        <xsl:text disable-output-escaping="yes">    &lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>

        

        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>       
        
    </xsl:template> 
    
</xsl:stylesheet>

