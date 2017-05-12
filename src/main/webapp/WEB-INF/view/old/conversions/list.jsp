<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/eurodyn.tld" prefix="ed" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--<html:xhtml/>--%>

<ed:breadcrumbs-push label="Stylesheets" level="1"/>


<c:if test="${stylesheet.stylesheetListHolder}">

  <c:if test="${stylesheet.permissions =='ssiPrm'}">
    <div id="operations">
      <ul>
        <li><a href="addStylesheetForm"><spring:message code="label.stylesheet.add"/></a></li>
      </ul>
    </div>
  </c:if>

  <h1 class="documentFirstHeading">
    <spring:message code="label.stylesheet.handcoded"/>
  </h1>

  <%-- include Error display --%>
  <tiles:insertDefinition name="Error"/>

  <div class="visualClear">&nbsp;</div>


  <c:if test="${stylesheet.stylesheetListHolder.stylesheetList}">
    <form:form action="/deleteStylesheet" method="post">

      <table id="tbl_stylesheets" class="display datatable" width="100%">
        <c:if test="${stylesheet.permissions == 'ssdPrm'}">
          <col style="width:5%"/>
        </c:if>
        <col/>
        <col/>
        <col/>
        <col style="width:140px"/>
        <thead>
        <tr>
          <c:if test="${stylesheet.permissions == 'ssdPrm'}">
            <th scope="col" class="scope-col"></th>
          </c:if>
          <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.file"/></th>
          <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.title"/></th>
          <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.type"/></th>
          <th scope="col" class="scope-col"><spring:message code="label.table.stylesheet.modified"/></th>
          <th style="display:none"/>
        </tr>
        </thead>
        <tbody>
      <%--id="stylesheet" name="stylesheet.stylesheetListHolder" property="stylesheetList"      type="Stylesheet">--%>
        <c:forEach varStatus="index" items="stylesheet.stylesheetListHolder.stylesheetList">
          <bean:define id="stylesheetId" name="stylesheet" property="convId"/>
          <tr class="${index.intValue() % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <c:if test="${stylesheet.permissions == 'ssdPrm'}">
              <td style="text-align:center">
                <input type="radio" name="conversionId" value="${stylesheetId}"></input>
              </td>
            </c:if>
            <td>
                <%--paramId="stylesheetId" paramName="stylesheet" paramProperty="convId"--%>
              <html:link page="/old/conversions/${stylesheetId}" title="View stylesheet">
                <bean:write name="stylesheet" property="xslFileName"/>
              </html:link>&#160;
            </td>
            <td>
              <bean:write name="stylesheet" property="description"/>
            </td>
            <td>
              <bean:write name="stylesheet" property="type"/>
            </td>
            <td style="font-size:0.8em;">
              <bean:write name="stylesheet" property="modified"/>
            </td>
            <td style="display:none">
              <bean:write name="stylesheet" property="lastModifiedTime" format="yyyy-MM-dd HH:mm:ss"/>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <c:if test="${stylesheet.permissions == 'ssdPrm'}">
        <br/>
        <div class="boxbottombuttons">
          <input type="button" class="button" value="<spring:message code="label.schema.delete"/>"
                 onclick="return submitAction(1,'deleteStylesheet');"/>
        </div>
      </c:if>
    </form:form>
  </c:if>
  <c:if test="${!stylesheet.stylesheetListHolder.stylesheetList}">
    <div class="advice-msg">
      <spring:message code="label.stylesheet.noHandCodedConversions"/>
    </div>
  </c:if>

  <div class="visualClear">&nbsp;</div>

</c:if>
