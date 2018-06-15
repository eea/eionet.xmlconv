<%@ include file="/WEB-INF/view/old/taglibs.jsp" %>

<c:set var="username" value="${sessionScope['user']}" />

<div id="container">

  <div id="toolribbon">
    <div id="lefttools">
      <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
      <a id="ewlink" href="http://ew.eea.europa.eu/">EnviroWindows</a>
    </div>
    <div id="righttools">
      <c:choose>
        <c:when test="${empty username}">
          <a id="loginlink" href="<c:url value="${loginUrl}"/>" title="Login">Login</a>
        </c:when>
        <c:otherwise>
          <a id="logoutlink" href="/login/logout" title="Logout">Logout
            <span>(${username})</span></a>
        </c:otherwise>
      </c:choose>
      <%--<a href="javascript:openWindow(applicationRoot+'/help/index.jsp','olinehelp');" title="Help">Online Help</a>--%>
      <%--<a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>--%>
      <%--<a id="fullscreenlink" href="javascript:toggleFullScreenMode()" title="Switch to/from full screen mode"><span>Switch to/from full screen mode</span></a>--%>
      <a id="acronymlink" href="http://www.eionet.europa.eu/acronyms" title="Look up acronyms"><span>Acronyms</span></a>
      <form action="https://google.com/search" method="get">
        <div id="freesrchform">
          <label class="simple" for="freesrchfld">Search</label>
          <%--onfocus="if (this.value == 'Search the site')--%>
          <%--this.value = '';"--%>
          <%--onblur="if (this.value == '')--%>
          <%--this.value = 'Search the site';"--%>
          <input type="text" id="freesrchfld" name="q" placeholder="Search the site" />
          <input type="hidden" name="sitesearch" value="${appHost}"/>
          <input id="freesrchbtn" type="image" src="<c:url value='/static/images/button_go.gif'/>" alt="Go"/>
        </div>
      </form>
    </div>
  </div> <!-- toolribbon -->

  <div id="pagehead">
    <a href="/"><img src="<c:url value='/static/images/eea-print-logo.gif'/>" alt="Logo" id="logo"/></a>
    <div id="networktitle">Eionet</div>
    <div id="sitetitle">Conversion and Quality Assessment Service</div>
    <div id="sitetagline"></div>
  </div> <!-- pagehead -->

  <div id="menuribbon">
    <%@ include file="dropdownmenus.txt" %>
  </div> <!-- menuribbon -->

  <div id="portal-breadcrumbs" class="breadcrumbtrail">
    <div class="breadcrumbhead">You are here:</div>
    <div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu"
                                                  title="European Environment Information and Observation Network (Eionet)">Eionet</a>
    </div>
  </div> <!-- breadcrumbtrail -->

  <div id="leftcolumn" class="localnav">
    <ul>
      <%--<li>JSP menu</li>
      <ed:menuItem action="/projects" title="Schemas">
        <spring:message code="label.menu.projects"/>
      </ed:menuItem>--%>
      <ed:menuItem action="/schemas" title="Schemas">
        <spring:message code="label.menu.schemas"/>
      </ed:menuItem>
      <ed:menuItem action="/conversions" title="Handcoded Stylesheets">
        <spring:message code="label.menu.stylesheets"/>
      </ed:menuItem>
      <ed:menuItem action="/conversions/generated" title="Generated Stylesheets">
        <spring:message code="label.menu.stylesheetsGenerated"/>
      </ed:menuItem>
      <ed:menuItem action="/converter" title="Converter">
        <spring:message code="label.menu.converter"/>
      </ed:menuItem>
      <ed:menuItem action="/validation" title="Validate XML">
        <spring:message code="label.conversion.tab.validation"/>
      </ed:menuItem>
      <ed:menuItem action="/workqueue" title="QA jobs">
        <spring:message code="label.menu.QAJobs"/>
      </ed:menuItem>
      <ed:menuItem action="/scripts" title="QA Scripts">
        <spring:message code="label.menu.queries"/>
      </ed:menuItem>
      <ed:menuItem action="/qaSandbox" title="QA Sandbox">
        <spring:message code="label.menu.xqsendbox"/>
      </ed:menuItem>
      <ed:menuItem action="/xmlFiles" title="XML Files">
        <spring:message code="label.menu.xmlfiles"/>
      </ed:menuItem>
      <ed:hasPermission username="username" acl="host" permission="v">
        <ed:menuItem action="/hosts" title="Hosts">
          <spring:message code="label.menu.hosts"/>
        </ed:menuItem>
      </ed:hasPermission>
      <ed:hasPermission username="username" acl="config" permission="u">
        <ed:menuItem action="/admin/purge" title="Administration tools">
          <spring:message code="label.menu.admintools"/>
        </ed:menuItem>
      </ed:hasPermission>
      <ed:hasPermission username="username" acl="serverstatus" permission="v">
        <ed:menuItem action="/webstatus" title="View the server status">
          <spring:message code="label.menu.serverstatus"/>
        </ed:menuItem>
      </ed:hasPermission>
    </ul>
  </div> <!-- leftcolumn -->

  <div id="workarea">
