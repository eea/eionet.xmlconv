<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cr="http://cr.eionet.europa.eu/ontologies/contreg.rdf#">
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
        <xsl:text disable-output-escaping="yes">/rdf#"&#xd;&#xa; </xsl:text>
<!--CR namespace  -->
        <xsl:text disable-output-escaping="yes">xmlns:cr="http://cr.eionet.europa.eu/ontologies/contreg.rdf#"&gt;&#xd;&#xa; </xsl:text>

        <xsl:element name="xsl:output"><xsl:attribute name="method">xml</xsl:attribute></xsl:element>

        <xsl:element name="xsl:param"><xsl:attribute name="name">instance</xsl:attribute><xsl:attribute name="select">''</xsl:attribute></xsl:element>

  <!--   TEMPLATE match ="/"  -->
        <xsl:element name="xsl:template"><xsl:attribute name="match">/</xsl:attribute>
            <xsl:element name="rdf:RDF">
                <xsl:element name="xsl:apply-templates"><xsl:attribute name="select"><xsl:value-of select="concat('dd', parentNS, ':', identifier, '/dd', parentNS, ':Row')"/></xsl:attribute></xsl:element>
            </xsl:element>
        </xsl:element>

<!--   TEMPLATE match ="Row"  -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="match"><xsl:value-of select="concat('dd', parentNS, ':', identifier, '/dd',parentNS, ':Row')"/></xsl:attribute>
            <xsl:element name="rdf:Description">
                <xsl:element name="xsl:attribute">
                    <xsl:attribute name="name">rdf:ID</xsl:attribute>
                    <xsl:element name="xsl:value-of"><xsl:attribute name="select">generate-id()</xsl:attribute></xsl:element>
                </xsl:element>
                <xsl:element name="rdf:type">
                    <xsl:attribute name="rdf:resource"><xsl:value-of select="concat($dd_domain, '/tables/', tableid, '/rdf/', identifier)"/></xsl:attribute>
                </xsl:element>

                <xsl:text disable-output-escaping="yes">&lt;!-- known by DD --&gt;&#xd;&#xa;</xsl:text>

                <xsl:element name="xsl:for-each">
                    <xsl:attribute name="select">*</xsl:attribute>
                    <xsl:element name="xsl:element">
                        <xsl:attribute name="name">{concat('elem:',local-name())}</xsl:attribute>
                        <xsl:element name="xsl:value-of"><xsl:attribute name="select">.</xsl:attribute></xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:stylesheet&gt;&#xd;&#xa;</xsl:text>
    </xsl:template>

</xsl:stylesheet>

