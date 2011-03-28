<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" version="1.0" encoding="utf-8" omit-xml-declaration="yes" indent="yes"/>
    <xsl:param name="templateselect" select=" 'header' "/>
    <xsl:param name="aligntable" select=" 'center' "/>
    <xsl:param name="cellclick" select=" 'false' "/>
    <xsl:param name="cellclickJS" select=" 'cellClick(event);return false;' "/>
    <xsl:param name="galleryPath" select="'/WebDashboards/images/gallery/'"/>
    <xsl:template match="/">
      <table cellpadding="0" cellspacing="0" border="0">
                <xsl:choose>
                    <xsl:when test="$cellclick='true'">
                          <xsl:attribute name="id">temp</xsl:attribute>
                          <xsl:attribute name="width">95%</xsl:attribute>
                          <xsl:attribute name="align">center</xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                          <xsl:attribute name="width">100%</xsl:attribute>
                      </xsl:otherwise>

                  </xsl:choose>
                <xsl:for-each select="ui-templates/template[@id=$templateselect]/row">

                <tr>
                   <xsl:for-each select="cell">
                        <td>
                            <xsl:if test="$cellclick='true' ">
                            <xsl:attribute name="onclick"><xsl:value-of select="$cellclickJS"/></xsl:attribute>
                            </xsl:if>

                                <xsl:attribute name="style">
                                <xsl:for-each select="style/*">
                                    <xsl:value-of select="name(.)"/>
                                    <xsl:text>:</xsl:text>
                                    <xsl:value-of select="."/>
                                    <xsl:text>;</xsl:text>
                                </xsl:for-each>
                            </xsl:attribute>
                            <xsl:if test="@type='LinkWithPic'">
                                <a >
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="link"/>
                                    </xsl:attribute>
                                   <xsl:element name="img">
                                        <xsl:attribute name="src">
                                            <xsl:value-of select="$galleryPath"/><xsl:value-of select="content"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="alt">
                                            <xsl:value-of select="content"/>
                                        </xsl:attribute>
                                    </xsl:element>
                                </a>
                            </xsl:if>
                            <xsl:if test="@type='Link'">
                                <a >
                                    <xsl:attribute name="href">
                                        <xsl:value-of select="link"/>
                                    </xsl:attribute>
                                    <xsl:value-of select="content"/>
                                </a>
                            </xsl:if>
                            <xsl:if test="@type='Picture'">
                                 <xsl:element name="img">
                                    <xsl:attribute name="src">
                                        <xsl:value-of select="$galleryPath"/><xsl:value-of select="content"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="content"/>
                                    </xsl:attribute>
                                </xsl:element>
                            </xsl:if>
                            <xsl:if test="@type='Text'">
                                <xsl:value-of select="content"/>

                            </xsl:if>
                            <xsl:if test="@type='blank'">
                                <xsl:if test="$cellclick='true'"><xsl:text>EMPTY CELL!!!</xsl:text></xsl:if>
                            </xsl:if>
                        </td>
                    </xsl:for-each>
                </tr>

        </xsl:for-each>
        </table>
    </xsl:template>
</xsl:stylesheet>
