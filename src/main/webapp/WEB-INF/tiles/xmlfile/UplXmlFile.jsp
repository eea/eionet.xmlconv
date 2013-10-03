<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*"%>
<%@ taglib uri="/WEB-INF/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<html:xhtml/>

<ed:breadcrumbs-push label="Uploaded XML files" level="1" />

<logic:present name="xmlfiles.uploaded">

    <logic:equal name="ssiPrm" value="true"  name="xmlfiles.uploaded" property="ssiPrm" >
        <div id="operations">
          <ul>
              <li><a href="addUplXmlFileForm"><bean:message key="label.uplXmlFile.add" /></a></li>
            </ul>
        </div>
    </logic:equal>

    <h1 class="documentFirstHeading">
        <bean:message key="label.xmlfiles.uploaded"/>
    </h1>

    <%-- include Error display --%>
    <tiles:insert definition="Error" />

    <div class="visualClear">&nbsp;</div>

    <logic:present name="xmlfiles" name="xmlfiles.uploaded" property="xmlfiles" >
        <html:form action="/deleteUplXmlFile" method="post">
            <table class="datatable" width="100%">
                <logic:equal name="ssdPrm" value="true"  name="xmlfiles.uploaded" property="ssuPrm" >
                    <col style="width:5%"/>
                </logic:equal>
                <logic:equal name="ssuPrm" value="true"  name="xmlfiles.uploaded" property="ssuPrm" >
                    <col style="width:5%"/>
                </logic:equal>
                <col style="width:30%"/>
                <col/>
                <col style="width:25%"/>
                <thead>
                    <tr>
                        <logic:equal name="ssdPrm" value="true"  name="xmlfiles.uploaded" property="ssuPrm" >
                            <th scope="col">&#160;</th>
                        </logic:equal>
                        <logic:equal name="ssuPrm" value="true"  name="xmlfiles.uploaded" property="ssuPrm" >
                            <th scope="col">&#160;</th>
                        </logic:equal>
                        <th scope="col"><span title="XML File"><bean:message key="label.table.uplXmlFile.xmlfile"/></span></th>
                        <th scope="col"><span title="Title"><bean:message key="label.table.uplXmlFile.title"/></span></th>
                        <th scope="col"><span title="Last Modified"><bean:message key="label.lastmodified"/></span></th>
                    </tr>
                </thead>
                <tbody>
                    <logic:iterate indexId="index" id="xmlfile" name="xmlfiles.uploaded" property="xmlfiles" type="UplXmlFile">
                        <tr <%=(index.intValue() % 2 == 1)? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
                            <logic:equal name="ssdPrm" value="true"  name="xmlfiles.uploaded" property="ssdPrm" >
                                <td align="center" >
                                    <bean:define id="fileId" name="xmlfile" property="id" />
                                    <input type="radio" name="xmlfileId" value="${fileId}" />
                                </td>
                            </logic:equal>
                            <logic:equal name="ssuPrm" value="true"  name="xmlfiles.uploaded" property="ssuPrm" >
                                <td align="center" >
                                    <a href="editUplXmlFileForm?xmlfileId=<bean:write name="xmlfile" property="id" />">
                                        <img src="<bean:write name="webRoot"/>/images/edit.gif" alt="<bean:message key="label.edit" />" title="edit XML file" /></a>
                                </td>
                            </logic:equal>
                            <td>
                                <a  href='<bean:write name="webRoot"/>/xmlfile/<bean:write name="xmlfile" property="fileName" />' title="<bean:write name="xmlfile" property="fileName" />">
                                    <bean:write name="xmlfile" property="fileName" />
                                </a>
                            </td>
                            <td>
                                <bean:write name="xmlfile" property="title" />
                            </td>
                            <td>
                                <logic:notEqual name="fileExists" value=""  name="xmlfile" property="lastModified" >
                                    <bean:write name="xmlfile" property="lastModified" />
                                </logic:notEqual>
                                <logic:equal name="fileNotExists" value=""  name="xmlfile" property="lastModified" >
                                    <span style="color:red"><bean:message key="label.fileNotFound"/></span>
                                </logic:equal>
                            </td>
                        </tr>
                    </logic:iterate>
                    <tr>
                        <td valign="top" colspan="4">
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="boxbottombuttons">
                <logic:equal name="ssdPrm" value="true"  name="xmlfiles.uploaded" property="ssdPrm" >
                    <input type="button"  class="button" value="<bean:message key="label.delete"/>" onclick="return submitAction(1,'deleteUplXmlFile');" />
                    <input type="button"  class="button" value="Rename" onclick="return submitAction(1,'renameUplXmlFileForm');" />
                </logic:equal>
            </div>
        </html:form>
    </logic:present>
    <logic:notPresent name="xmlfiles" name="xmlfiles.uploaded" property="xmlfiles" >
        <div class="advice-msg">
            <bean:message key="label.uplXmlFile.noXmlFiles"/>
        </div>
    </logic:notPresent>
    <div class="visualClear">&nbsp;</div>

</logic:present>



