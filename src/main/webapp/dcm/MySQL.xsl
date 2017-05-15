<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xml"/>
    <xsl:param name="dd_domain" select="'true'"/>
    <xsl:param name="dd_ns_url" select="concat('=&quot;',$dd_domain,'/namespace.jsp?ns_id=')"/>

    <xsl:template match="/">
        <xsl:apply-templates select="table"/>
    </xsl:template>

    <xsl:template match="table">
        <xsl:text xml:space="default" disable-output-escaping="yes">&#xd;&#xa;&lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"&#xd;&#xa;    xmlns:dd</xsl:text>
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
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Dumping data for table&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">        &lt;/xsl:if&gt;&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- Start SQL command &#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">INSERT INTO `</xsl:text>
        <xsl:value-of select="identifier"/>
        <xsl:text disable-output-escaping="yes">`            &lt;xsl:call-template name="fields"/&gt;</xsl:text>
        <xsl:text disable-output-escaping="yes"> VALUES ('&lt;xsl:value-of select="@status"/&gt;',&#xd;&#xa;</xsl:text>
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
            <xsl:element name="xsl:choose">
                <xsl:element name="xsl:when"><xsl:attribute name="test">count($row/*[local-name()= $elemIdentifier])=0 or string-join($row/*[local-name()= $elemIdentifier ],'')=''</xsl:attribute>null</xsl:element>
                <xsl:element name="xsl:otherwise">&apos;<xsl:element name="xsl:value-of"><xsl:attribute name="select">replace(string-join($row/*[local-name()= $elemIdentifier ],$multiValueSeparator),&quot;'&quot;,&quot;''&quot;)</xsl:attribute></xsl:element>&apos;</xsl:element>
            </xsl:element>
            <xsl:element name="xsl:if"><xsl:attribute name="test">position()!=last()</xsl:attribute>,</xsl:element>
        </xsl:element>
        <xsl:text disable-output-escaping="yes">);&#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>
        <xsl:text disable-output-escaping="yes">&lt;/xsl:template&gt;&#xd;&#xa;</xsl:text>
        <!--   TEMPLATE name="create-table"  -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="name">create-table</xsl:attribute>
            <xsl:text disable-output-escaping="yes">-- SQL scripts for MySQL&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">-- Table structure for table&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">-- Set database encoding to UTF-8&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40101 SET NAMES utf8 */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE="NO_AUTO_VALUE_ON_ZERO" */;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">--&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">-- Start SQL command &#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">CREATE TABLE `</xsl:text>
            <xsl:value-of select="identifier"/>
            <xsl:text disable-output-escaping="yes">` (status varchar(10), </xsl:text>


            <xsl:element name="xsl:for-each" namespace="http://www.w3.org/1999/XSL/Transform">
                <xsl:attribute name="select">$elementsMetadata/element</xsl:attribute>
                <xsl:text disable-output-escaping="yes">`</xsl:text>
                <xsl:element name="xsl:value-of"><xsl:attribute name="select">identifier</xsl:attribute></xsl:element>
                <xsl:text disable-output-escaping="yes">` </xsl:text>
                <xsl:element name="xsl:call-template">
                    <xsl:attribute name="name">getFieldType</xsl:attribute>
                    <xsl:element name="xsl:with-param">
                        <xsl:attribute name="name">name</xsl:attribute>
                        <xsl:attribute name="select">identifier</xsl:attribute>
                    </xsl:element>
                </xsl:element>
                <xsl:element name="xsl:if">
                    <xsl:attribute name="test">position()!=last()</xsl:attribute>,
                </xsl:element>
            </xsl:element>
            <xsl:text disable-output-escaping="yes">) DEFAULT CHARSET=utf8;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">-- End SQL command &#xd;&#xa;</xsl:text>
        </xsl:element>

        <!--   TEMPLATE name="fields"  -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="name">fields</xsl:attribute>
            <xsl:text disable-output-escaping="yes">(status, &#xd;&#xa;</xsl:text>
            <xsl:element name="xsl:for-each" namespace="http://www.w3.org/1999/XSL/Transform">
                <xsl:attribute name="select">$elementsMetadata/element</xsl:attribute>
                <xsl:text disable-output-escaping="yes">`</xsl:text>
                <xsl:element name="xsl:value-of">
                    <xsl:attribute name="select">identifier</xsl:attribute>
                </xsl:element>
                <xsl:text disable-output-escaping="yes">`</xsl:text>
                <xsl:element name="xsl:if"><xsl:attribute name="test">position()!=last()</xsl:attribute>,</xsl:element>
            </xsl:element>
            <xsl:text disable-output-escaping="yes">) </xsl:text>
        </xsl:element>

        <!--   TEMPLATE name ="getFieldType"  -->
        <xsl:element name="xsl:template">
            <xsl:attribute name="name">getFieldType</xsl:attribute>
            <xsl:element name="xsl:param">
                <xsl:attribute name="name">name</xsl:attribute>
                <xsl:attribute name="select">''</xsl:attribute>
            </xsl:element>
            <xsl:text disable-output-escaping="yes">&lt;xsl:choose&gt;&#xd;&#xa;</xsl:text>
            <xsl:for-each select="//elements/element">
                <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$name = '</xsl:text>
                <xsl:value-of select="identifier"/>
                <xsl:text disable-output-escaping="yes">'"&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:variable name="type"&gt;</xsl:text>
                <xsl:value-of select="type"/>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:variable&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:choose&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'integer'"&gt;</xsl:text>
                <xsl:if test="length=''">numeric(64)</xsl:if>
                <xsl:if test="length!=''">
                    <xsl:if test="length &gt; 64">numeric(64)</xsl:if>
                    <xsl:if test="length &lt;= 64">numeric(<xsl:value-of select="length"/>)</xsl:if>
                </xsl:if>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'string'"&gt;</xsl:text>
                <xsl:if test="length=''">text</xsl:if>
                <xsl:if test="length!=''">
                    <xsl:if test="length &gt; 255">text</xsl:if>
                    <xsl:if test="length &lt;= 255">varchar(<xsl:value-of select="length"/>)</xsl:if>
                </xsl:if>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'float'"&gt;</xsl:text>
                <xsl:if test="length=''">numeric(64<xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                </xsl:if>)</xsl:if>
                <xsl:if test="length!=''">
                    <xsl:if test="length &gt; 64">numeric(64<xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                    </xsl:if>)</xsl:if>
                    <xsl:if test="length &lt;= 64">numeric(<xsl:value-of select="length"/>
                        <xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                        </xsl:if>)</xsl:if>
                </xsl:if>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:when test="$type = 'double'"&gt;</xsl:text>
                <xsl:if test="length=''">numeric(64<xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                </xsl:if>)</xsl:if>
                <xsl:if test="length!=''">
                    <xsl:if test="length &gt; 64">numeric(64<xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                    </xsl:if>)</xsl:if>
                    <xsl:if test="length &lt;= 64">numeric(<xsl:value-of select="length"/>
                        <xsl:if test="precision!=''">,<xsl:value-of select="precision"/>
                        </xsl:if>)</xsl:if>
                </xsl:if>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;text&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;/xsl:when&gt;&#xd;&#xa;</xsl:text>
            </xsl:for-each>
            <xsl:text disable-output-escaping="yes">&lt;xsl:otherwise&gt;text&lt;/xsl:otherwise&gt;&#xd;&#xa;</xsl:text>
            <xsl:text disable-output-escaping="yes">&lt;/xsl:choose&gt;&#xd;&#xa;</xsl:text>
        </xsl:element>



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
