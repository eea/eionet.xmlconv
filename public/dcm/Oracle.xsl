<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output  method="xml"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>
    
    <xsl:template match="table">
        <xsl:text  xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"&#xd;&#xa;    xmlns:dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        <xsl:value-of select="$dd_ns_url"/>
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">" xmlns:dd</xsl:text>
        <xsl:value-of select="correspondingNS"/>
        <xsl:value-of select="$dd_ns_url"/>
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
        <xsl:text disable-output-escaping="yes">INSERT INTO "</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">"            &lt;xsl:call-template name="fields"/&gt;</xsl:text>        
        <xsl:text disable-output-escaping="yes"> VALUES ('&lt;xsl:value-of select="@status"/&gt;',&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:for-each select="*"&gt;&lt;xsl:if test=".=''"&gt;null&lt;/xsl:if&gt;&lt;xsl:if test=".!=''"&gt;'&lt;xsl:call-template name="globalReplace"&gt;</xsl:text>
		<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="outputString" select="."/&gt;</xsl:text>
		<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="target" select='"&amp;apos;"'/&gt;</xsl:text>
		<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="replacement" select='"&amp;apos;&amp;apos;"'/&gt;</xsl:text>
		<xsl:text disable-output-escaping="yes">&lt;/xsl:call-template&gt;'&lt;/xsl:if&gt;&lt;xsl:if test="position()!=last()"&gt;,&lt;/xsl:if&gt;&lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">);&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>                        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
   <!--   TEMPLATE name="create-table"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>     
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="create-table"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- SQL scripts for Oracle&#xd;&#xa;</xsl:text>        
        <xsl:text disable-output-escaping="yes">-- Table structure for table&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Start SQL command &#xd;&#xa;</xsl:text>          
        <xsl:text disable-output-escaping="yes">CREATE TABLE "</xsl:text>
        <xsl:value-of select="substring(identifier,0,30)"/>
        <xsl:text disable-output-escaping="yes">" (status nvarchar2(10), &#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:for-each select="*"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">  &lt;xsl:if test="string-length(local-name())     </xsl:text>
        <xsl:text disable-output-escaping="no">    &gt;     </xsl:text>
        <xsl:text disable-output-escaping="yes">   30"&gt;    </xsl:text>
        <xsl:text disable-output-escaping="yes">   "</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="substring(local-name(),0,28)" /&gt; </xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:number value="position()" format="01" /&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">"</xsl:text>
        <xsl:text disable-output-escaping="yes">  &lt;/xsl:if&gt;                                              </xsl:text>
        <xsl:text disable-output-escaping="yes">  &lt;xsl:if test="string-length(local-name()) </xsl:text>
        <xsl:text disable-output-escaping="no">   &lt;    </xsl:text>
        <xsl:text disable-output-escaping="yes">   31"&gt;    </xsl:text>                
        <xsl:text disable-output-escaping="yes">  "</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="local-name()" /&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">"</xsl:text>
        <xsl:text disable-output-escaping="yes">  &lt;/xsl:if&gt;                                              </xsl:text>
        <xsl:text disable-output-escaping="yes">  &lt;xsl:value-of select="' '"/&gt;&lt;xsl:call-template name="getFieldType"&gt;&lt;xsl:with-param name="name" select="local-name()"/&gt;&lt;/xsl:call-template&gt;&#xd;&#xa;</xsl:text>         


        <xsl:text disable-output-escaping="yes">&lt;xsl:if test="position()!=last()"&gt;,&#xd;&#xa;&lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">);&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>         
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>

   <!--   TEMPLATE name="fields"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>     
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="fields"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">(status, &#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:for-each select="*"&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">"</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="local-name()" /&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">"</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:if test="position()!=last()"&gt;,&#xd;&#xa;&lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:for-each&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">) </xsl:text>
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
            
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'integer'"&gt;</xsl:text>            
            <xsl:if test="length=''">number(38)</xsl:if><xsl:if test="length!=''"><xsl:if test="length &gt; 38">number(38)</xsl:if><xsl:if test="length &lt;= 38">number(<xsl:value-of select="length"/>)</xsl:if></xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            
            
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'string'"&gt;</xsl:text>            
            <xsl:if test="length=''">clob</xsl:if><xsl:if test="length!=''"><xsl:if test="length &gt; 2000">clob</xsl:if><xsl:if test="length &lt;= 2000">nvarchar2(<xsl:value-of select="length"/>)</xsl:if></xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            
            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'float'"&gt;</xsl:text>            
            <xsl:if test="length=''">number(38<xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if><xsl:if test="length!=''"><xsl:if test="length &gt; 38">number(38<xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if><xsl:if test="length &lt;= 38">number(<xsl:value-of select="length"/><xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if></xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            
            

            <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'double'"&gt;</xsl:text>            
            <xsl:if test="length=''">number(38<xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if><xsl:if test="length!=''"><xsl:if test="length &gt; 38">number(38<xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if><xsl:if test="length &lt;= 38">number(<xsl:value-of select="length"/><xsl:if test="precision!=''">,<xsl:value-of select="precision"/></xsl:if>)</xsl:if></xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>

            
            <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;clob&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text> 
            <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>            
        </xsl:for-each>
        
        <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;clob&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>

		<xsl:text disable-output-escaping="yes">&lt;xsl:template name="globalReplace"&gt;&#xd;&#xa;</xsl:text>
			<xsl:text disable-output-escaping="yes">&lt;xsl:param name="outputString"/&gt;&#xd;&#xa;</xsl:text>
			<xsl:text disable-output-escaping="yes">&lt;xsl:param name="target"/&gt;&#xd;&#xa;</xsl:text>
			<xsl:text disable-output-escaping="yes">&lt;xsl:param name="replacement"/&gt;&#xd;&#xa;</xsl:text>
				<xsl:text disable-output-escaping="yes">&lt;xsl:choose&gt;&#xd;&#xa;</xsl:text>
					<xsl:text disable-output-escaping="yes">&lt;xsl:when test="contains($outputString,$target)"&gt;&#xd;&#xa;</xsl:text>
						<xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="concat(substring-before($outputString,$target),$replacement)"/&gt;&#xd;&#xa;</xsl:text>
						<xsl:text disable-output-escaping="yes">&lt;xsl:call-template name="globalReplace"&gt;&#xd;&#xa;</xsl:text>
							<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="outputString" select="substring-after($outputString,$target)"/&gt;&#xd;&#xa;</xsl:text>
							<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="target" select="$target"/&gt;&#xd;&#xa;</xsl:text>
							<xsl:text disable-output-escaping="yes">&lt;xsl:with-param name="replacement" select="$replacement"/&gt;&#xd;&#xa;</xsl:text>
						<xsl:text disable-output-escaping="yes">&lt;/xsl:call-template&gt;&#xd;&#xa;</xsl:text>
					<xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
					<xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
				<xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="$outputString"/&gt;&#xd;&#xa;</xsl:text>
				<xsl:text disable-output-escaping="yes">&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
			<xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text>
		<xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>       
        
    </xsl:template> 
    
</xsl:stylesheet>

