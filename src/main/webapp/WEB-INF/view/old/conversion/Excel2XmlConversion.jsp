<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*,eionet.gdem.Properties"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
<div style="width:100%;">

        <tiles:insert definition="ConverterTabs">
            <tiles:put name="selectedTab" value="excel2xml" />
        </tiles:insert>

        <ed:breadcrumbs-push label="Spreadsheet to XML" level="1" />
        <h1><spring:message code="label.conversion.excel2xml.heading"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

        <html:form action="/excel2XmlConversion" method="get" >
        <table class="datatable">
            <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.url"/>
                  </th>
            </tr>
            <tr>
                <td>
                    <label for="inpUrl"><spring:message code="label.conversion.insertExcelUrl"/></label>
                </td>
            </tr>
            <tr>
                <td>
                    <html:text name="ExcelConversionForm" property="url" style="width: 45em;" styleId="inpUrl" size="200"/>
                </td>
            </tr>
            <tr>
                 <th scope="col" class="scope-col">
                    <spring:message code="label.conversion.sheets"/>
                  </th>
            </tr>
            <tr>
                <td>
                    <spring:message code="label.conversion.excel.format"/>
                </td>
            </tr>
            <tr>
                <td>
                    <html:radio property="split" styleId="split1" value="all" onclick="sheet.disabled=true" />
                    <label for="split1"><spring:message code="label.conversion.excel.allsheets"/></label>
                </td>
            </tr>
            <tr>
                <td>
                    <html:radio property="split"  styleId="split2" value="split" onclick="sheet.disabled=false"/>
                    <label for="split2"><spring:message code="label.conversion.excel.sheetname"/></label>
                    <html:text property="sheet" onfocus="split[1].checked=true"/>
                </td>
            </tr>
                <tr>
                  <td>
                    <html:checkbox name="ExcelConversionForm" property="showConversionLog" styleId ="chkConversion" />
                    <label for="chkConversion"><spring:message code="label.conversion.excel.showConversionLog"/></label>
                  </td>
                </tr>
                <tr>
                  <td align="center">
                    <html:submit styleClass="button">
                        <spring:message code="label.conversion.convert"/>
                    </html:submit>
                  </td>
                </tr>
                <logic:notEmpty name="ExcelConversionForm" property="conversionLog">
                    <tr>
                      <td><bean:write name="ExcelConversionForm" property="conversionLog" filter="false"/></td>
                    </tr>
                </logic:notEmpty>
                <logic:empty name="ExcelConversionForm" property="conversionLog">
                    <tr>
                        <td><spring:message code="label.conversion.excel.warning"/>
                        </td>
                    </tr>
                </logic:empty>
        </table>
      <!--/fieldset-->
    </html:form>
</div>
