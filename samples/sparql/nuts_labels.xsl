<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:sparql="http://www.w3.org/2005/sparql-results#"> 
	<xsl:output method="text" encoding="utf-8"/>
	
	<xsl:variable name="sparqlUrl" select="'http://localhost:8890/sparql'"/>
	
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="region">
		<xsl:value-of select="@code"/> - <xsl:call-template name="getNutsLabel"><xsl:with-param name="code"><xsl:value-of select="@code"/></xsl:with-param></xsl:call-template>
	
	</xsl:template>
	
	
	<xsl:template name="getNutsLabel">
	
		<xsl:param name="code" select="''"/>
		
		<xsl:variable name="sparql"><![CDATA[
			select * where {?s <http://www.w3.org/2000/01/rdf-schema#label> ?o .
 
				?s <http://ec.europa.eu/eurostat/ramon/ontologies/geographic.rdf#code> ]]>'<xsl:value-of select="$code"/>'<![CDATA[
}
 
		]]></xsl:variable>
		
		<xsl:variable name="sparql_url">
			<xsl:call-template name="getSparqlEndPointUrl">
<xsl:with-param name="sparql"><xsl:value-of select="normalize-space($sparql)"/></xsl:with-param>
</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="document($sparql_url)/sparql:sparql/sparql:results/sparql:result/sparql:binding[@name='o']/sparql:literal/text()"/>
	</xsl:template>
	
	
	
	<xsl:template name="getSparqlEndPointUrl">

		<xsl:param name="sparql" select="''"/>
		
		<xsl:variable name="sparql-encoded" select="fn:encode-for-uri($sparql)"/>
    		<xsl:variable name="defaultGraph" select="fn:encode-for-uri('http://ec.europa.eu/eurostat/ramon/rdfdata/nuts2008.rdf')"/>
	   	<xsl:variable name="uriParams" select="concat('default-graph-uri=', $defaultGraph, '&amp;query=', $sparql-encoded , '&amp;format=application/sparql-results+xml')"/>
    		
    		<xsl:value-of select="concat($sparqlUrl, '?', $uriParams)"/>
	</xsl:template>
</xsl:stylesheet>
