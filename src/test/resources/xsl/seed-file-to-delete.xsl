<?xml version='1.0' encoding='UTF-8'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     xmlns="http://www.w3.org/1999/xhtml"
version="1.0" exclude-result-prefixes="xml">
    <xsl:output method="xml" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" omit-xml-declaration="yes"/>
    <xsl:preserve-space elements="legal-texts published description measures impact species-names species-concerned"/>

    <xsl:param name="envelopeurl" select="'unknown'"/>

    <xsl:template match="report">
    <html><xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang"/></xsl:attribute><head>
            <title>Factsheet in envelope: <xsl:value-of select="$envelopeurl"/></title>
            <style type="text/css"><![CDATA[

img {
        border: 2px solid #ccc;
        margin: 10px;
}
h2 {
    font-size: 140%;
    font-style:italic;
    font-family: arial, verdana, sans-serif;
}
h3 {
    font-size: 140%;
    font-family: arial, verdana, sans-serif;
}
h1 {
    font-size: 160%;
    padding-bottom: 0.5em;
    border-bottom: 1px solid #999999;
}
body {
        font-size: 80%;
        font-family: verdana, helvetica, arial, sans-serif;
        color: #333;
}
caption {
        font-family: vardana, verdana, helvetica, arial, sans-serif;
        text-align: left;
        font-weight: bold;
        border: 1px solid #bbbbbb;
}
th {
        background-color:#f6f6f6;
        text-align:left;
        vertical-align: top;
        font-weight: normal;
        color: black;
}
table {
        font-size: 100%;
        border: 1px solid #bbb;
        width: 100%;
        margin: 0 0 2em 0;
}
table table {
        border: 0px solid #bbb;
        margin: 0;
}
th, td {
        font-size: 100%;
        border: 1px solid #bbb;
}
.has_table {
        border: 0px solid #bbb;
        padding: 0;
}
th.header_3 {
    padding: 0.4em 0;
    font-size: 120%;
    font-weight: bold;
}
th.tlabel {
    width: 300px;
}
.header_info {
    margin: 20px 0px;
    padding: 5px;
    font-size: 120%;
    border: 1px dashed #999999;
    background-color: #f0f0f0;
}
.header_info div {
    margin: 5px;
}
.number {
    text-align: right;
}
.req_row {
    width: 33%;
}

]]></style>
        </head>
        <body>
            <h1>General report (Annex A)</h1>
            <xsl:apply-templates select="legal-framework"/>
            <h2>
                <xsl:value-of select="regional/@label"/>
            </h2>
            <table>
                <tbody>
                    <xsl:apply-templates select="regional"/>
                </tbody>
            </table>
            <xsl:apply-templates select="management-tools"/>
            <xsl:apply-templates select="conservation-measures"/>
            <xsl:apply-templates select="deterioration-measures"/>
            <xsl:apply-templates select="plan-measures"/>
            <xsl:apply-templates select="financing"/>
            <xsl:apply-templates select="coherence-measures"/>
            <xsl:apply-templates select="surveillance-system"/>
            <xsl:apply-templates select="protection-measures"/>
            <xsl:apply-templates select="supporting-measures"/>
        </body>
    </html>
</xsl:template>

<xsl:template match="member-state"/>

<xsl:template match="legal-framework|management-tools|conservation-measures|deterioration-measures|plan-measures|financing|coherence-measures|surveillance-system">
    <h2>
        <xsl:value-of select="@label"/>
    </h2>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="regional"><!-- Section 2 -->
    <tr>
        <th colSpan="3" style="padding-right: 0em; padding-left: 0em; font-size: 120%; padding-bottom: 0.3em; padding-top: 0.3em; background-color: white">
            <strong>Regional level
                <xsl:value-of select="region"/>
            </strong>
        </th>
    </tr>
    <xsl:apply-templates/>
</xsl:template>

<xsl:template match="community-importance|areas-of-conservation">
    <tr>
        <th colspan="3">
            <strong><xsl:value-of select="@label"/></strong>
        </th>
    </tr>
    <tr>
        <th><xsl:value-of select="@label"/></th>
        <th>No</th>
        <th>Area</th>
    </tr>
    <tr>
        <th>
            Total
        </th>
        <td class="number">
            <xsl:call-template name="text-or-NA">
                <xsl:with-param name="text" select="total/number"/>
            </xsl:call-template>
        </td>
        <td class="number">
            <xsl:call-template name="text-or-NA">
                <xsl:with-param name="text" select="total/area"/>
            </xsl:call-template>
        </td>
    </tr>
    <tr>
        <th>
            Marine
        </th>
        <td class="number">
            <xsl:call-template name="text-or-NA">
                <xsl:with-param name="text" select="marine/number"/>
            </xsl:call-template>
        </td>
        <td class="number">
            <xsl:call-template name="text-or-NA">
                <xsl:with-param name="text" select="marine/area"/>
            </xsl:call-template>
        </td>
    </tr>
</xsl:template>

<xsl:template match="region"/>

<xsl:template match="management-plans">
    <h3>
        <xsl:value-of select="@label"/>
    </h3>
    <xsl:apply-templates select="adopted-number"/>
    <xsl:apply-templates select="preparation-number"/>
    <xsl:apply-templates select="created-number"/>
    <table border="1">
        <caption>
            <xsl:value-of select="plans-list/@label"/>
        </caption>
        <xsl:apply-templates select="plans-list"/>
    </table>
</xsl:template>

<xsl:template match="other-planning">
    <h3>
        <xsl:value-of select="@label"/>
    </h3>
    <xsl:apply-templates select="included-number"/>
    <table border="1">
    <xsl:apply-templates select="other-list"/>
    </table>
</xsl:template>

<xsl:template match="non-planning">
    <h3>
        <xsl:value-of select="@label"/>
    </h3>
    <xsl:apply-templates select="number-non-plan"/>
    <table border="1">
    <xsl:apply-templates select="not-plan-list"/>
    </table>
</xsl:template>

<xsl:template match="plan-measures"><!-- Section 6 -->
    <h2>
        <xsl:value-of select="@label"/>
    </h2>
    <xsl:apply-templates select="necessary-number"/>
    <xsl:apply-templates select="requested-number"/>
    <table border="1">
        <caption>
            <xsl:value-of select="necessary-list/@label"/>
        </caption>
        <xsl:apply-templates select="necessary-list"/>
    </table>
    <xsl:apply-templates select="impact"/>
</xsl:template>

<xsl:template match="financing"><!-- Section 7 -->
    <h2>
        <xsl:value-of select="@label"/>
    </h2>
    <table border="1" style="width:auto">
        <caption>
            <xsl:value-of select="estimated-average-annual-costs[1]/@label"/>
        </caption>
        <tbody>
            <xsl:apply-templates select="estimated-average-annual-costs"/>
        </tbody>
    </table>

    <xsl:apply-templates select="measures"/>
    <table border="1" style="width:auto">
        <caption>
            <xsl:value-of select="estimated-annual-costs[1]/@label"/>
        </caption>
        <tbody>
        <xsl:apply-templates select="estimated-annual-costs"/>
        </tbody>
    </table>
    <xsl:apply-templates select="cofinancing-by-eu" mode="currency"/>
    <table border="1">
        <caption>
            <xsl:value-of select="cofinancing-list/@label"/>
        </caption>
        <xsl:apply-templates select="cofinancing-list"/>
    </table>
</xsl:template>

<xsl:template match="protection-measures"><!-- Section 10 -->
    <h2>
        <xsl:value-of select="@label"/>
    </h2>
    <table border="1">
        <caption>
            <xsl:value-of select="requisites/@label"/>
        </caption>
        <xsl:apply-templates select="requisites"/>
    </table>
    <table border="1">
        <caption>
            <xsl:value-of select="control-systems/@label"/>
        </caption>
        <xsl:apply-templates select="control-systems"/>
    </table>
    <table border="1">
        <caption>
            <xsl:value-of select="species-taking/@label"/>
        </caption>
            <xsl:apply-templates select="species-taking"/>
    </table>
    <table border="1">
        <caption>
            <xsl:value-of select="indiscriminate-means/@label"/>
        </caption>
            <xsl:apply-templates select="indiscriminate-means"/>
    </table>
</xsl:template>

<xsl:template match="supporting-measures"><!-- Section 11 -->
    <h2>
        <xsl:value-of select="@label"/>
    </h2>
    <div style="border: 1px solid #ccc;">
        <div style="background-color: #f0f0f0; border-bottom: 1px solid #ccc; padding: 0.1em 0 0.1em 0"><strong><xsl:value-of select="research/@label"/></strong></div>
        <xsl:apply-templates select="research/main-efforts"/>
        <xsl:apply-templates select="research/published"/>
    </div><br />
    <table border="1">
        <caption>
            <xsl:value-of select="reintroduction-of-species/@label"/>
        </caption>
            <xsl:apply-templates select="reintroduction-of-species"/>
    </table>
    <table border="1">
        <caption>
            <xsl:value-of select="introduction-of-nonnative-species/@label"/>
        </caption>
            <xsl:apply-templates select="introduction-of-nonnative-species"/>
    </table>
    <div style="border: 1px solid #ccc;">
        <div style="background-color: #f0f0f0; border-bottom: 1px solid #ccc; padding: 0.1em 0 0.1em 0"><strong><xsl:value-of select="education/@label"/></strong></div>
        <xsl:apply-templates select="education/measures"/>
        <xsl:apply-templates select="education/published"/>
    </div>
</xsl:template>

<xsl:template match="plans-list|other-list|not-plan-list|necessary-list|cofinancing-list|control-systems|species-taking|indiscriminate-means|reintroduction-of-species|introduction-of-nonnative-species">
    <xsl:if test="position()=1">
        <xsl:call-template name="header"/>
    </xsl:if>
    <tr>
        <xsl:apply-templates mode="table"/>
    </tr>
</xsl:template>

<xsl:template name="header_req">
    <tr>
        <xsl:for-each select="*">
            <th class="req_row">
                <xsl:value-of select="@label"/>
            </th>
        </xsl:for-each>
    </tr>
</xsl:template>

<xsl:template match="requisites">
    <xsl:for-each select="*">
        <tr>
            <th><xsl:value-of select="@label"/></th>
            <td class="preserve">
                <xsl:call-template name="break"><xsl:with-param name="text" select="."/></xsl:call-template>
                <xsl:if test="string-length(.)=0">
                    N/A
                </xsl:if>
            </td>
        </tr>
    </xsl:for-each>
    <tr>
        <td colspan="2" style="border: none"><hr /></td>
    </tr>
</xsl:template>

<!-- TABLE MODE START -->
<xsl:template match="co-financing" mode="table">
    <td class="number">
        <xsl:call-template name="text-or-NA">
            <xsl:with-param name="text" select="."/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@currency"/>
    </td>
</xsl:template>
<xsl:template match="*" mode="table">
    <td>
        <xsl:call-template name="text-or-NA">
            <xsl:with-param name="text" select="."/>
        </xsl:call-template>
    </td>
</xsl:template>

<xsl:template match="legal-texts|published|description|measures|impact|species-names|species-concerned" mode="table">
    <td class="preserve">
        <xsl:call-template name="break"><xsl:with-param name="text" select="."/></xsl:call-template>
        <xsl:if test="string-length(.)=0">
            N/A
        </xsl:if>
    </td>
</xsl:template>
<!-- TABLE MODE END -->

<xsl:template match="legal-texts|published|description|measures|impact|species-names|species-concerned|main-efforts">
    <p>
        <strong><xsl:value-of select="@label"/>:</strong><br/>
        <xsl:if test="string-length(.)=0">
            N/A
        </xsl:if>
        <xsl:call-template name="break"><xsl:with-param name="text" select="."/></xsl:call-template>
    </p>
</xsl:template>

<xsl:template match="*">
    <p>
        <strong><xsl:value-of select="@label"/>: </strong>
        <xsl:call-template name="text-or-NA">
            <xsl:with-param name="text" select="."/>
        </xsl:call-template>
    </p>
</xsl:template>

<xsl:template match="*" mode="currency">
    <p>
        <b>
            <xsl:value-of select="@label"/>:
        </b>
        <xsl:call-template name="text-or-NA">
            <xsl:with-param name="text" select="."/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:call-template name="text-or-NA">
            <xsl:with-param name="text" select="@currency"/>
        </xsl:call-template>
    </p>
</xsl:template>

<xsl:template name="header">
    <tr>
        <xsl:for-each select="*">
            <th>
                <xsl:value-of select="@label"/>
            </th>
        </xsl:for-each>
    </tr>
</xsl:template>

<!-- Replaces line breaks with <br/> tags -->
<xsl:template name="break"><xsl:param name="text" select="."/>
    <xsl:choose>
        <xsl:when test="contains($text, '&#10;')">
            <xsl:value-of select="substring-before($text, '&#10;')"/>
            <br/>
            <xsl:call-template name="break">
                <xsl:with-param name="text" select="substring-after($text, '&#10;')"/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$text"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- Returns N/A if the parameter is empty string, otherwise returns the string itself -->
    <xsl:template name="text-or-NA">
        <xsl:param name="text" select="."/>
        <xsl:choose>
            <xsl:when test="string-length($text)=0">
                N/A
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


<xsl:template match="estimated-average-annual-costs|estimated-annual-costs">
<tr>
<td>Year <xsl:value-of select="@year"/></td>
<td class="number"><xsl:value-of select="text()"/></td>
<td><xsl:value-of select="@currency"/></td>
</tr>
</xsl:template>

<xsl:template match="@currency"/>
<xsl:template match="@label"/>

</xsl:stylesheet>
