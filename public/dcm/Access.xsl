<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output  method="xml"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>
    
    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"&#xd;&#xa;    xmlns:dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">="http://rubi:8080/dd/namespace.jsp?ns_id=</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">" xmlns:dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:text disable-output-escaping="yes">="http://rubi:8080/dd/namespace.jsp?ns_id=</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:text disable-output-escaping="yes">"&gt;&#xd;&#xa;    &lt;xsl:output method="text"/&gt;&#xd;&#xa;</xsl:text>
 
  <!--   TEMPLATE match ="/"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">    &lt;xsl:template match="/"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;xsl:apply-templates select="dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">/dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:Row"/&gt;&#xd;&#xa;</xsl:text>
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
        <xsl:text disable-output-escaping="yes">            &lt;xsl:call-template name="create-table"/&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--\n\r&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Dumping data for table&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--\n\r&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Start SQL command &#xd;&#xa;</xsl:text>                
        <xsl:text disable-output-escaping="yes">INSERT INTO </xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes"> VALUES ('&lt;xsl:value-of select="@status"/&gt;',&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:for-each select="*"&gt;&lt;xsl:if test=".=''"&gt;null&lt;/xsl:if&gt;&lt;xsl:if test=".!=''"&gt;'&lt;xsl:value-of select="." /&gt;'&lt;/xsl:if&gt;&lt;xsl:if test="position()!=last()"&gt;,&lt;/xsl:if&gt;&lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">);&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>                
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
   <!--   TEMPLATE name="create-table"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>     
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="create-table"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- SQL scripts for MS Access&#xd;&#xa;</xsl:text>        
        <xsl:text disable-output-escaping="yes">-- Table structure for table&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Start SQL command &#xd;&#xa;</xsl:text>        
        <xsl:text disable-output-escaping="yes">CREATE TABLE </xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes"> (status nvarchar(10), &#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">    &lt;xsl:for-each select="*"&gt;&#xd;&#xa;</xsl:text>
    <!--      <xsl:text disable-output-escaping="yes">[&lt;xsl:value-of select="local-name()" /&gt;]&lt;xsl:value-of select="' '"/&gt;&lt;xsl:call-template name="getFieldType"&gt;&lt;xsl:with-param name="name" select="local-name()"/&gt;&lt;/xsl:call-template&gt;&#xd;&#xa;</xsl:text>-->
        <xsl:text disable-output-escaping="yes">[&lt;xsl:value-of select="translate(local-name(),'.','_')" /&gt;]&lt;xsl:value-of select="' '"/&gt;&lt;xsl:call-template name="getFieldType"&gt;&lt;xsl:with-param name="name" select="local-name()"/&gt;&lt;/xsl:call-template&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:if test="position()!=last()"&gt;,&lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">);&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
 <!--   TEMPLATE name ="getFieldType"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>       
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="getFieldType"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:param name="name" select="''"/&gt;&#xd;&#xa;</xsl:text>        
        <xsl:text disable-output-escaping="yes">&lt;xsl:choose&gt;&#xd;&#xa;</xsl:text>
        
        <xsl:for-each select="//elements/element">            
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$name = '</xsl:text>
            <xsl:value-of select="identifier"/>
            <xsl:text disable-output-escaping="yes">'"&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:variable name="type"&gt;</xsl:text><xsl:value-of select="type"/><xsl:text disable-output-escaping="yes">&lt;/xsl:variable&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:choose&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'integer'"&gt;int&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'string'"&gt;text(</xsl:text><xsl:value-of select="length"/><xsl:text disable-output-escaping="yes">) WITH COMP&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'float'"&gt;double&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;text WITH COMP&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text> 
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>            
        </xsl:for-each>
        
        <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;text WITH COMP&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>       
        
    </xsl:template> 
    
</xsl:stylesheet>

