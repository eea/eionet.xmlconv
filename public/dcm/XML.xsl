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
        <xsl:text disable-output-escaping="yes">"&gt;&#xd;&#xa;    &lt;xsl:output method="xml"/&gt;&#xd;&#xa;</xsl:text>
 
  <!--   TEMPLATE match ="/"  -->
        
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">    &lt;xsl:template match="/"&gt;&#xd;&#xa;</xsl:text>
        <delivery>
        <xsl:text disable-output-escaping="yes">        &lt;xsl:apply-templates select="dd</xsl:text>
        
        <xsl:value-of select="parentNS"/>
        <xsl:text disable-output-escaping="yes">:</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">/dd</xsl:text>
        <xsl:value-of select="parentNS"/>
        
        <xsl:text disable-output-escaping="yes">:Row"/&gt;&#xd;&#xa;</xsl:text>
        </delivery>
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
        <xsl:text disable-output-escaping="yes">            &lt;xsl:call-template name="table-def"/&gt;&#xd;&#xa;</xsl:text>      
        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;data&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>

        
        <row>        
        <xsl:text disable-output-escaping="yes">&lt;xsl:for-each select="*"&gt;</xsl:text>

        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>        


        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="local-name()"/&gt;</xsl:text>

        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>        

        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="."/&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;/</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>        
        <xsl:text disable-output-escaping="yes">&lt;xsl:value-of select="local-name()"/&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:for-each&gt;</xsl:text>
        </row>

        <xsl:text disable-output-escaping="yes">        &lt;xsl:if test="position()=last()"&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;xsl:text disable-output-escaping="yes"&gt;</xsl:text>
        <xsl:text disable-output-escaping="no">&lt;/data&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:text&gt;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>

        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
   <!--   TEMPLATE name="table-def"  -->
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>     
        <xsl:text disable-output-escaping="yes">&lt;xsl:template name="table-def"&gt;&#xd;&#xa;</xsl:text>

        <table>
        <xsl:for-each select="//elements/element">
        
            <xsl:text disable-output-escaping="yes">&lt;elem&gt;</xsl:text>                        
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;name&gt;</xsl:text>           
            <xsl:value-of select="identifier"/>    
            <xsl:text disable-output-escaping="yes">&lt;/name&gt;</xsl:text>           
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;type&gt;</xsl:text>           
            <xsl:value-of select="type"/>
            <xsl:text disable-output-escaping="yes">&lt;/type&gt;</xsl:text>            
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;length&gt;</xsl:text>           
            <xsl:value-of select="length"/>
            <xsl:text disable-output-escaping="yes">&lt;/length&gt;</xsl:text>           
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;precision&gt;</xsl:text>           
            <xsl:value-of select="precision"/>
            <xsl:text disable-output-escaping="yes">&lt;/precision&gt;</xsl:text>            
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;multiValueDelim&gt;</xsl:text>           
            <xsl:value-of select="multiValueDelim"/>
            <xsl:text disable-output-escaping="yes">&lt;/multiValueDelim&gt;</xsl:text>            
            <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;/elem&gt;</xsl:text>                       
        </xsl:for-each>
        </table>
        
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        
        <xsl:text disable-output-escaping="yes">&#xd;&#xa;</xsl:text>       
        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>       
        
    </xsl:template> 
    
</xsl:stylesheet>

