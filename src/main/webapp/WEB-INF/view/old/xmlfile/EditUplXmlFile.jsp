<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>

<html:xhtml/>
        <ed:breadcrumbs-push label="Edit XML File" level="2" />
        <h1><bean:message key="label.title.uplXmlFile.edit"/></h1>

        <%-- include Error display --%>
        <tiles:insertDefinition name="Error" />

            <html:form action="/editUplXmlFile" method="post" enctype="multipart/form-data">
              <table class="formtable">
                <col class="labelcol"/>
                <col class="entrycol"/>
                <tr class="zebraeven">
                    <td>
                        <label class="question">
                        <bean:message key="label.uplXmlFile.xmlfile"/>
                    </label>
                  </td>
                  <td>
                        <a  href="<bean:write name="EditUplXmlFileForm" property="xmlFilePath" /><bean:write name="EditUplXmlFileForm" property="xmlFileName" />" title="<bean:write name="EditUplXmlFileForm" property="xmlFileName" />">
                            <bean:write name="EditUplXmlFileForm" property="xmlFileName" />
                        </a>&#160;&#160;
                        (<bean:message key="label.lastmodified"/>:
                        <logic:present name="EditUplXmlFileForm" property="lastModified">
                            <bean:write property="lastModified" name="EditUplXmlFileForm"/>
                        </logic:present>
                        <logic:equal value=""  name="EditUplXmlFileForm" property="lastModified" >
                            <span style="color:red"><bean:message key="label.fileNotFound"/></span>
                        </logic:equal>
                        )
                        <html:hidden  property="xmlfileId" />
                        <html:hidden  property="xmlFileName" />
                  </td>
                </tr>
                <tr>
                    <td>
                  </td>
                  <td>
                    <html:file property="xmlFile" styleId="fileXml" size="68" />
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
                        <bean:message key="label.ok"/>
                    </html:submit>
                    <html:cancel styleClass="button">
                        <bean:message key="label.cancel"/>
                    </html:cancel>
                  </td>
                </tr>
              </table>
            </html:form>
