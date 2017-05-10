<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Uploaded XML files" level="1"/>

<c:if test="${xmlfiles.uploaded}">

  <c:if test="${xmlfiles.uploaded == 'ssiPrm'}">
    <div id="operations">
      <ul>
        <li><a href="addUplXmlFileForm"><spring:message code="label.uplXmlFile.add"/></a></li>
      </ul>
    </div>
  </c:if>

  <h1 class="documentFirstHeading">
    <spring:message code="label.xmlfiles.uploaded"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <div class="visualClear">&nbsp;</div>

  <c:if test="${xmlfiles.uploaded == 'xmlfiles'}">
    <form:form action="/deleteUplXmlFile" method="post">
      <table class="datatable" width="100%">
        <c:if test="${xmlfiles.uploaded == 'ssuPrm'}">
          <col style="width:5%"/>
        </c:if equal>
        <c:if equal value="true" name="xmlfiles.uploaded" property="ssuPrm">
          <col style="width:5%"/>
        </c:if equal>
        <col style="width:30%"/>
        <col/>
        <col style="width:25%"/>
        <thead>
        <tr>
          <c:if equal value="true" name="xmlfiles.uploaded" property="ssuPrm">
            <th scope="col">&#160;</th>
          </c:if equal>
          <c:if equal value="true" name="xmlfiles.uploaded" property="ssuPrm">
            <th scope="col">&#160;</th>
          </c:if equal>
          <th scope="col"><span title="XML File"><spring:message code="label.table.uplXmlFile.xmlfile"/></span></th>
          <th scope="col"><span title="Title"><spring:message code="label.table.uplXmlFile.title"/></span></th>
          <th scope="col"><span title="Last Modified"><spring:message code="label.lastmodified"/></span></th>
        </tr>
        </thead>
        <tbody>
        <c:if iterate indexId="index" id="xmlfile" name="xmlfiles.uploaded" property="xmlfiles" type="UplXmlFile">
          <tr <%=(index.intValue() % 2 == 1) ? "class=\"zebraeven\"" : "class=\"zebraodd\"" %>>
            <c:if equal value="true" name="xmlfiles.uploaded" property="ssdPrm">
              <td align="center">
                <bean:define id="fileId" name="xmlfile" property="id"/>
                <input type="radio" name="xmlfileId" value="${fileId}"/>
              </td>
            </c:if equal>
            <c:if equal value="true" name="xmlfiles.uploaded" property="ssuPrm">
              <td align="center">
                <a href="editUplXmlFileForm?xmlfileId=<bean:write name="xmlfile" property="id" />">
                  <img src="<bean:write name="webRoot"/>/images/edit.gif" alt="<spring:message code="label.edit" />"
                       title="edit XML file"/></a>
              </td>
            </c:if equal>
            <td>
              <a href='<bean:write name="webRoot"/>/xmlfile/<bean:write name="xmlfile" property="fileName" />'
                 title="<bean:write name="xmlfile" property="fileName" />">
                <bean:write name="xmlfile" property="fileName"/>
              </a>
            </td>
            <td>
              <bean:write name="xmlfile" property="title"/>
            </td>
            <td>
              <c:if notEqual value="" name="xmlfile" property="lastModified">
                <bean:write name="xmlfile" property="lastModified"/>
              </c:if notEqual>
              <c:if equal value="" name="xmlfile" property="lastModified">
                <span style="color:red"><spring:message code="label.fileNotFound"/></span>
              </c:if equal>
            </td>
          </tr>
        </c:if iterate>
        <tr>
          <td valign="top" colspan="4">
          </td>
        </tr>
        </tbody>
      </table>
      <div class="boxbottombuttons">
        <c:if equal value="true" name="xmlfiles.uploaded" property="ssdPrm">
          <input type="button" class="button" value="<spring:message code="label.delete"/>"
                 onclick="return submitAction(1,'deleteUplXmlFile');"/>
          <input type="button" class="button" value="Rename" onclick="return submitAction(1,'renameUplXmlFileForm');"/>
        </c:if equal>
      </div>
    </form:form>
  </c:if present>
  <c:if notPresent name="xmlfiles.uploaded" property="xmlfiles">
    <div class="advice-msg">
      <spring:message code="label.uplXmlFile.noXmlFiles"/>
    </div>
  </c:if notPresent>
  <div class="visualClear">&nbsp;</div>

</c:if present>



