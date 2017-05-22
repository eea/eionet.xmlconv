<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Stylesheets" level="1"/>

<c:set var="permissions" scope="page" value="${sessionScope['stylesheet.permissions']}" />
<c:if test="${!empty conversions}">

  <c:if test="${permissions.ssiPrm}">
    <div id="operations">
      <ul>
        <li><a href="/conversions/add"><spring:message code="label.stylesheet.add"/></a></li>
      </ul>
    </div>
  </c:if>

  <h1 class="documentFirstHeading">
    <spring:message code="label.stylesheet.handcoded"/>
  </h1>


  <div class="visualClear">&nbsp;</div>


  <c:if test="${!empty conversions.stylesheetList}">
    <form:form r="/deleteStylesheet" method="post">

      <table id="tbl_stylesheets" class="display datatable" width="100%">
        <c:if test="${permissions.ssdPrm}">
          <col style="width:5%"/>
        </c:if>
        <col/>
        <col/>
        <col/>
        <col style="width:140px"/>
        <thead>
        <tr>
          <c:if test="${permissions.ssdPrm}">
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
        <c:forEach varStatus="i" items="${conversions.stylesheetList}" var="conversion">
          <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
            <c:if test="${permissions.ssdPrm}">
              <td style="text-align:center">
                <input type="radio" name="conversionId" value="${conversion.convId}"></input>
              </td>
            </c:if>
            <td>
              <a href="/conversions/${conversion.convId}" title="View stylesheet">
                  ${conversion.xslFileName}
              </a>&#160;
            </td>
            <td>
                ${conversion.description}
            </td>
            <td>
                ${conversion.type}
            </td>
            <td style="font-size:0.8em;">
                ${conversion.modified}
            </td>
            <td style="display:none">
              <fmt:formatDate value="${conversion.lastModifiedTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <c:if test="${permissions.ssdPrm}">
        <br/>
        <div class="boxbottombuttons">
          <button type="submit" class="button" value="delete">
            <spring:message code="label.schema.delete"/>
          </button>
        </div>
      </c:if>
    </form:form>
  </c:if>
  <c:if test="${!empty conversions.stylesheetList}">
    <div class="advice-msg">
      <spring:message code="label.stylesheet.noHandCodedConversions"/>
    </div>
  </c:if>

  <div class="visualClear">&nbsp;</div>

</c:if>
