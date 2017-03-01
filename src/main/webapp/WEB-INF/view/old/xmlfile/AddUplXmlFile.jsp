<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %><%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="Upload XML File" level="1" />
        <h1><spring:message code="label.title.uplXmlFile.add"/></h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />

            <html:form action="/addUplXmlFile" method="post" enctype="multipart/form-data">
              <table class="formtable">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr class="zebraeven">
                    <td>
                    <label class="question required" for="fileXml">
                        <spring:message code="label.uplXmlFile.xmlfile"/>
                    </label>
                  </td>
                  <td>
                    <html:file property="xmlfile" styleId="fileXml" size="68" />
                  </td>
                </tr>
                <tr>
                    <td>
                    <label class="question" for="txtTitle">
                        <spring:message code="label.uplXmlFile.title"/>
                    </label>
                  </td>
                  <td>
                    <html:text property="title" style="width:400px" styleId="txtTitle" />
                  </td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>
                    <html:submit styleClass="button">
                        <spring:message code="label.uplXmlFile.upload"/>
                    </html:submit>
                    <html:cancel styleClass="button">
                        <spring:message code="label.cancel"/>
                    </html:cancel>
                  </td>
                </tr>
              </table>
            </html:form>
