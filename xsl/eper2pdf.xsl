<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
>
    <!-- generate PDF page structure -->
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="page"
                  page-height="29.7cm" 
                  page-width="21cm"
                  margin-top="1cm" 
                  margin-bottom="2cm" 
                  margin-left="2.5cm" 
                  margin-right="2.5cm"
                >
                    <fo:region-before extent="3cm"/>
                    <fo:region-body margin-top="3cm"/>
                    <fo:region-after extent="1.5cm"/>
                </fo:simple-page-master>
	            </fo:layout-master-set>

							<fo:page-sequence master-reference="page">
								<fo:flow flow-name="xsl-region-body">
									<fo:block><xsl:apply-templates/></fo:block>
								</fo:flow>
               </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <!-- process reports -->
    <xsl:template match="report">
       <fo:block font-size="14pt">
					<xsl:apply-templates select="SubmissionDate"/>
					<xsl:apply-templates select="Country"/>
					<xsl:apply-templates select="ReportYear"/>
					Measurements:
					<xsl:apply-templates select="Facility"/>
			 </fo:block>
    </xsl:template>

    <xsl:template match="Facility">
			  <fo:block font-size="14pt" margin-left="2cm">
					<xsl:apply-templates select="ParentCompanyName"/>
					<xsl:apply-templates select="Emission"/>
				</fo:block>
		</xsl:template>

		<xsl:template match="Emission">
		  <fo:block font-size="12pt" margin-left="4cm">
				<xsl:value-of select="PollutantName"/> : <xsl:value-of select="EmissionValue"/>
			</fo:block>
		</xsl:template>

		<xsl:template match="SubmissionDate">
			<fo:block font-size="22pt">
        Submission Date: <xsl:value-of select="."/>
			</fo:block>
		</xsl:template>

		<xsl:template match="ReportYear">
        <fo:block font-size="22pt" color="green">
            Year: <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>
		<!-- convert sections to XSL-FO headings -->
    <xsl:template match="Country">
        <fo:block font-size="24pt" color="red" font-weight="bold">
            Country: <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>

</xsl:stylesheet>