<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp"%>
<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Uploaded XML files" level="1"/>

<c:if test="${!empty xmlfiles}">

  <c:if test="${!xmlfiles.ssiPrm}">
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
  <%--<tiles:insertDefinition name="Error"/>--%>

  <div class="visualClear">&nbsp;</div>

  <c:if test="${!empty xmlfiles.xmlfiles}">
    <form:form servletRelativeAction="xmlfile/deleteUplXmlFile" method="post">
      <table class="datatable" width="100%">
        <c:if test="${xmlfiles.ssuPrm}">
          <col style="width:5%"/>
        </c:if>
        <c:if test="${xmlfiles.ssuPrm}">
          <col style="width:5%"/>
        </c:if>
        <col style="width:30%"/>
        <col/>
        <col style="width:25%"/>
        <thead>
        <tr>
          <c:if test="${xmlfiles.ssuPrm}">
            <th scope="col">&#160;</th>
          </c:if>
          <c:if test="${xmlfiles.ssuPrm}">
            <th scope="col">&#160;</th>
          </c:if>
          <th scope="col"><span title="XML File"><spring:message code="label.table.uplXmlFile.xmlfile"/></span></th>
          <th scope="col"><span title="Title"><spring:message code="label.table.uplXmlFile.title"/></span></th>
          <th scope="col"><span title="Last Modified"><spring:message code="label.lastmodified"/></span></th>
        </tr>
        </thead>
        <tbody>
          <%--type="UplXmlFile"--%>
        <c:forEach varStatus="i" items="${xmlfiles.xmlfiles}" var="xmlfile">
          <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <c:if test="${xmlfiles.ssdPrm}">
              <td align="center">
                ${xmlfile.id}
                <input type="radio" name="xmlfileId" value="${fileId}"/>
              </td>
            </c:if>
            <c:if test="${xmlfiles.ssuPrm}">
              <td align="center">
                <a href="xmlfiles/edit/${xmlfile.id}">
                  <img src="${webRoot}/images/edit.gif" alt="<spring:message code="label.edit" />"
                       title="edit XML file"/></a>
              </td>
            </c:if>
            <td>
              <a href='${webRoot}/xmlfile/${xmlfile.fileName}' title="${xmlfile.fileName}">
                ${xmlfile.fileName}
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
        <c:if test="${xmlfiles.ssdPrm}">
          <button name="action" class="button" value="delete"><spring:message code="label.delete"/></button>
          <button name="action" class="button" value="rename"><spring:message code="label.rename"/></button>
        </c:if>
      </div>
    </form:form>
  </c:if>
  <c:if test="${empty xmlfiles.xmlfiles}">
    <div class="advice-msg">
      <spring:message code="label.uplXmlFile.noXmlFiles"/>
    </div>
  </c:if>
  <div class="visualClear">&nbsp;</div>

</c:if>



