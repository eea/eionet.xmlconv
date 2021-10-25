<%@include file="/WEB-INF/view/old/taglibs.jsp" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<ed:breadcrumbs-push label="Workqueue" level="1"/>

<link rel="stylesheet" type="text/css" href="<c:url value='/static/js/DataTables/media/css/jquery.dataTables.min.css'/>"></link>
<link rel="stylesheet" type="text/css" href="<c:url value='/static/js/workqueue.css'/>"></link>
<link rel="stylesheet" type="text/css" href="<c:url value='/static/webjars/font-awesome/web-fonts-with-css/css/fontawesome-all.css'/>"></link>

<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/jquery.dataTables.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/js/workqueue.js'/>"></script>

<!--export buttons-->
<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/exportButtons/dataTables.buttons.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/exportButtons/jszip.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/exportButtons/buttons.html5.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/exportButtons/buttons.colVis.min.js'/>"></script>


<script type="text/javascript">
  // <![CDATA[
  var elementName = "jobID";
  isSelected = false;

  function toggleSelect() {
    elems = document.getElementsByTagName("input");

    if (isSelected == false) {
      for (i = 0; i < elems.length; i++)
        if (elems[i].value && elementName == elems[i].name) {
          elems[i].checked = true;
        }
      isSelected = true;
      document.getElementById("selectAll").value = "Deselect All";
      return isSelected;
    }
    else {
      for (i = 0; i < elems.length; i++)
        elems[i].checked = false;
      isSelected = false;
      document.getElementById("selectAll").value = "Select All";
      return isSelected;
    }
  }

  function countSelected() {
    var j = 0;
    elems = document.getElementsByTagName("input");
    for (i = 0; i < elems.length; i++)
      if (elems[i].checked == true && elementName == elems[i].name) {
        j++;
      }
    return j;
  }

  function doDelete() {
    if (countSelected() == 0) {
      alert('No jobs selected!');
      return false;
    }
    if (!confirm('Are you sure you want to delete the selected jobs?'))
      return false;

    document.getElementById('ACTION').value = '${Constants.WQ_DEL_ACTION}';
    document.getElementById('jobs').submit();
  }

  function doRestart() {
    if (countSelected() == 0) {
      alert('No jobs selected!');
      return false;
    }
    document.getElementById('ACTION').value = '${Constants.WQ_RESTART_ACTION}';
    document.getElementById('jobs').submit();
  }

  // ]]>
</script>

<h1>Jobs</h1>

<p>Currently there are following jobs in the queue...</p>
<div id="main_table">
  <form:form id="jobs" servletRelativeAction="/workqueue" method="post" modelAttribute="form">
    <form:errors path="*" cssClass="error-msg" element="div"/>
    <table id="workqueue_table" class="datatable results" width="100%" export="true" style="overflow-wrap: break-word;white-space: normal;">
      <col style="width:30px; text-align:right;"/>
      <col style="width:50px; text-align:right;"/>
      <col/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <thead>
      <tr>
        <th scope="col" class="scope-col details-control" colspan="2">Job ID</th>
        <th scope="col" class="scope-col">Document URL</th>
        <th scope="col" class="scope-col">Query script</th>
        <th scope="col" class="scope-col">Job Result</th>
        <th scope="col" class="scope-col">
          <div class="dropdown-container">
            <div class="dropdown-button noselect">
              <span class="dropdown-label">Status<i class="fas fa-filter"></i></span>
            </div>
            <div class="dropdown-content" style="display: none;">
              <div class="dropdown-list">
                <ul>
                  <li>
                    <input id="received" name="received" type="checkbox" checked="checked">
                    <label for="received">RECEIVED</label>
                  </li>
                  <li>
                    <input id="processing" name="processing" type="checkbox" checked="checked">
                    <label for="processing">PROCESSING</label>
                  </li>
                  <li>
                    <input id="ready" name="ready" type="checkbox" checked="checked">
                    <label for="ready">READY</label>
                  </li>
                  <li>
                    <input id="error" name="error" type="checkbox" checked="checked">
                    <label for="error">FATAL ERROR</label>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </th>
        <th scope="col" class="scope-col">Started at</th>
        <th scope="col" class="scope-col">Instance</th>
        <th scope="col" class="scope-col">Duration</th>
        <th scope="col" class="scope-col">Job type</th>
        <th scope="col" class="scope-col">Worker</th>
      </tr>
      </thead>
      <tbody>

      <c:forEach items="${jobList}" varStatus="i" var="job">
        <tr class="${i.index % 2 == 1 ? 'zebraodd' : 'zebraeven'}">
          <c:choose>
            <c:when test="${permissions.wqdPrm || permissions.wquPrm}">
              <td>
                <form:checkbox path="jobs" id="job_${job.jobId}" value="${job.jobId}"/>
              </td>
              <td class="details-control" id="selectableJobId">
                  <label for="job_${job.jobId}" style="cursor:pointer">${job.jobId}</label>
              </td>
            </c:when>
            <c:otherwise>
              <td/>
              <td class="details-control" id="selectableJobId">${job.jobId}</td>
            </c:otherwise>
          </c:choose>
          <td>
            <a href="${job.url}" rel="nofollow">${job.url}</a>
          </td>
          <td>
            <c:choose>
              <c:when test="${fn:startsWith(job.scriptFile, 'gdem')}">
                  <a href="/tmp/${job.scriptFile}" rel="nofollow">${job.scriptFile}</a>
              </c:when>
              <c:otherwise>
                  <a href="/scripts/${job.scriptId}" rel="nofollow">${job.scriptFile}</a>
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
              <c:when test="${!empty job.resultFile}">
                <c:choose>
                  <c:when test="${permissions.wqvPrm}">
                    <a href="${'tmp'}/${job.resultFile}" rel="nofollow">Job result</a>
                  </c:when>
                  <c:otherwise>
                    <div title="Log in to see job result">Job result</div>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                *** Not ready ***
              </c:otherwise>
            </c:choose>
          </td>
          <td>
              ${job.statusName}
          </td>
          <td>
              ${job.timestamp}
          </td>
          <td>
            <c:choose>
              <c:when test="${permissions.wqvPrm}">
                ${job.instance}
              </c:when>
              <c:otherwise>
                <div title="Log in to see system info">-</div>
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:choose>
              <c:when test="${not empty job.durationInProgress}">
                ${job.durationInProgress}
              </c:when>
            </c:choose>
          </td>
          <td>${job.jobType}</td>
          <td>${job.jobExecutorName}</td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div id="hidden_elements">
      <c:if test="${permissions.wqdPrm || permissions.wquPrm}">
        <c:if test="${permissions.wqdPrm}">
          <button class="button" type="submit" name="delete"
                  onclick="return confirm('Are you sure you want to delete the selected jobs?')">
            Delete
          </button>
        </c:if>
        <c:if test="${permissions.wquPrm}">
          <button class="button" type="submit" name="restart"
                  onclick="return confirm('Are you sure you want to restart the selected jobs?')">
            Restart
          </button>
        </c:if>
        <%--<input class="form-element" type="button" name="selectAll" id="selectAll" value="Select All"
               onclick="toggleSelect('jobID'); return false"/>--%>
        <%--onclick="toggleSelect('jobID'); return false"--%>
        <button class="button" type="button" id="selectAll">
          Select All
        </button>
      </c:if>
      <input type="hidden" name="ACTION" id="ACTION" value="delete"/>
      <input type="hidden" name="ID" value=""/>
    </div>
  </form:form>
</div>
<script>
  $("#selectAll").click(function () {
    $('input[type="checkbox"]').prop("checked", true);
  });


</script>