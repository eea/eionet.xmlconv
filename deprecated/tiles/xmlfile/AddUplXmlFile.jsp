<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>

        <ed:breadcrumbs-push label="Upload XML File" level="1" />
        <h1><bean:message key="label.title.uplXmlFile.add"/></h1>

        <%-- include Error display --%>
        <tiles:insert definition="Error" />

            <html:form action="/addUplXmlFile" method="post" enctype="multipart/form-data">
              <table class="formtable">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr class="zebraeven">
                    <td>
                    <label class="question required" for="fileXml">
                        <bean:message key="label.uplXmlFile.xmlfile"/>
                    </label>
                  </td>
                  <td>
                    <html:file property="xmlfile" styleId="fileXml" size="68" />
                  </td>
                </tr>
                <tr>
                    <td>
                    <label class="question" for="txtTitle">
                        <bean:message key="label.uplXmlFile.title"/>
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
                        <bean:message key="label.uplXmlFile.upload"/>
                    </html:submit>
                    <html:cancel styleClass="button">
                        <bean:message key="label.cancel"/>
                    </html:cancel>
                  </td>
                </tr>
              </table>
            </html:form>
