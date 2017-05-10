<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
        </c:if>
        <c:if test="${xmlfiles.uploaded == 'ssuPrm'}">
          <col style="width:5%"/>
        </c:if>
        <col style="width:30%"/>
        <col/>
        <col style="width:25%"/>
        <thead>
        <tr>
          <c:if test="${xmlfiles.uploaded =='ssuPrm'}">
            <th scope="col">&#160;</th>
          </c:if>
          <c:if test="${xmlfiles.uploaded == 'ssuPrm'}">
            <th scope="col">&#160;</th>
          </c:if>
          <th scope="col"><span title="XML File"><spring:message code="label.table.uplXmlFile.xmlfile"/></span></th>
          <th scope="col"><span title="Title"><spring:message code="label.table.uplXmlFile.title"/></span></th>
          <th scope="col"><span title="Last Modified"><spring:message code="label.lastmodified"/></span></th>
        </tr>
        </thead>
        <tbody>
          <%--type="UplXmlFile"--%>
        <c:forEach varStatus="index" items="xmlfiles.uploaded.xmlfiles">
          <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <c:if test="${xmlfiles.uploaded == 'ssdPrm'}">
              <td align="center">
                <bean:define id="fileId" name="xmlfile" property="id"/>
                <input type="radio" name="xmlfileId" value="${fileId}"/>
              </td>
            </c:if>
            <c:if test="${xmlfiles.uploaded == 'ssuPrm'}">
              <td align="center">
                <a href="editUplXmlFileForm?xmlfileId=<bean:write name="xmlfile" property="id" />">
                  <img src="<bean:write name="webRoot"/>/images/edit.gif" alt="<spring:message code="label.edit" />"
                       title="edit XML file"/></a>
              </td>
            </c:if>
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
                <%--<c:if notEqual value="" name="xmlfile" property="lastModified">--%>
              <c:choose>
                <c:when test="${xmlfile != lastModified}">
                  ${xmlfile.lastModified}
                </c:when>
                <c:otherwise>
                  <span style="color:red"><spring:message code="label.fileNotFound"/></span>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
        <tr>
          <td valign="top" colspan="4">
          </td>
        </tr>
        </tbody>
      </table>
      <div class="boxbottombuttons">
        <c:if test="${xmlfiles.uploaded == 'ssdPrm'}">
          <input type="button" class="button" value="<spring:message code="label.delete"/>"
                 onclick="return submitAction(1,'deleteUplXmlFile');"/>
          <input type="button" class="button" value="Rename" onclick="return submitAction(1,'renameUplXmlFileForm');"/>
        </c:if>
      </div>
    </form:form>
  </c:if>
  <c:if test="${xmlfiles.uploaded != 'xmlfiles'}">
    <div class="advice-msg">
      <spring:message code="label.uplXmlFile.noXmlFiles"/>
    </div>
  </c:if>
  <div class="visualClear">&nbsp;</div>

</c:if>



