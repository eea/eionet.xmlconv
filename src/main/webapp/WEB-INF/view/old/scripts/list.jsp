<%--<%@ page contentType="text/html; charset=UTF-8" import="eionet.gdem.dto.*" %>--%>
<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="All QA Scripts" level="1"/>

<c:set var="permissions" scope="page" value="${sessionScope['qascript.permissions']}" />

<link href="<c:url value='/static/webjars/jquery-ui/jquery-ui.css'/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="<c:url value='/static/webjars/jquery/jquery.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/webjars/jquery-ui/jquery-ui.js'/>"></script>
<script type="text/javascript" src="/resources/js/statusModal.js"></script>

<style>
  div#operations li {
    display: inline-block;
    margin:0px;
  }
</style>

<c:if test="${permissions.ssiPrm}">
  <div id="operations">
    <ul>
      <li>
        <button class="statusHelp" type="button" style="color:#00446A; background:#ecf4f5; cursor:pointer; border: 1px solid #cfe3e4; padding: 0.5em; border-radius:6px">QA scripts status info
        </button>
      </li>
      <li>
        <a href="/scripts/add"><spring:message code="label.qascript.add"/></a>
      </li>
    </ul>
  </div>
</c:if>

<h1 class="documentFirstHeading">
  <spring:message code="label.qascript.title"/>
</h1>




<div class="visualClear">&nbsp;</div>

<c:if test="${!empty scripts.qascripts}">
  <div style="width: 97%">
    <table class="datatable results" width="100%">
      <col/>
      <col/>
      <thead>
      <tr>
        <th scope="col" class="scope-col"><spring:message code="label.table.qascript.xmlschema"/></th>
        <th scope="col" class="scope-col"><spring:message code="label.table.qascript.qascripts"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach varStatus="i" items="${scripts.qascripts}" var="schema">
        <tr class="${i.index % 2 == 1 ? 'zebraeven' : 'zebraodd'}">
          <td title="${schema.schema}">
            <a href="/schemas/${schema.id}/scripts" title="view QA scripts for this XML Schema">
                ${schema.schema}
            </a>
          </td>
          <td>
            <c:if test="${!empty schema.qascripts}">
              <%--id="qascript" name="schema" scope="page" property="qascripts" type="QAScript">--%>
              <c:forEach items="${schema.qascripts}" var="script">
                <a href="/scripts/${script.scriptId}" title="label.qascript.tab.title">
                    ${script.shortName}
                </a>
                &#160;
              </c:forEach>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td valign="top" colspan="3">
        </td>
      </tr>
      </tbody>
    </table>
  </div>

  <div class="visualClear">&nbsp;</div>

</c:if>



