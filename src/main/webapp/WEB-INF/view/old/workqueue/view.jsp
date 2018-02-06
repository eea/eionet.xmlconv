<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<%--&lt;%&ndash; TODO REMOVE SCRIPTLET &ndash;%&gt;--%>
<%--<%--%>
  <%--response.setHeader("Pragma", "No-cache");--%>
  <%--response.setHeader("Cache-Control", "no-cache");--%>
  <%--response.setHeader("Cache-Control", "no-store");--%>
  <%--response.setDateHeader("Expires", 0);--%>
<%--%>--%>

<ed:breadcrumbs-push label="Workqueue" level="1"/>

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

<form:errors cssClass="error-msg" ></form:errors>

<p>Currently there are following jobs in the queue...</p>
<div id="main_table">
  <form:form id="jobs" servletRelativeAction="/workqueue/actions" method="post" modelAttribute="form">
    <table class="datatable results" width="100%">
      <col style="width:30px; text-align:right;"/>
      <col style="width:50px; text-align:right;"/>
      <col/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <col style="width:100px"/>
      <thead>
      <tr>
        <th scope="col" class="scope-col" colspan="2">job ID</th>
        <th scope="col" class="scope-col">Document URL</th>
        <th scope="col" class="scope-col">XQuery script</th>
        <th scope="col" class="scope-col">Job Result</th>
        <th scope="col" class="scope-col">status</th>
        <th scope="col" class="scope-col">Started at</th>
        <th scope="col" class="scope-col">Instance</th>
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
              <td>
                <label for="job_${job.jobId}">${job.jobId}</label>
              </td>
            </c:when>
            <c:otherwise>
              <td/>
              <td>${job.jobId}</td>
            </c:otherwise>
          </c:choose>
          <td>
            <a href="${job.url}" rel="nofollow">${job.url}</a>
          </td>
          <td>
              <c:choose>
                <c:when test="${job.scriptType != 'fme'}">
                  <a href="${job.scriptFile}" rel="nofollow">${job.scriptFile}</a>
                </c:when>
                <c:otherwise>
                  ${job.scriptType}
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
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div id="hidden_elements">
      <c:if test="${permissions.wqdPrm || permissions.wquPrm}">
        <c:if test="${permissions.wqdPrm}">
          <button class="button" type="submit" name="delete">
            Delete
          </button>
        </c:if>
        <c:if test="${permissions.wquPrm}">
          <button class="button" type="submit" name="restart">
            Restart
          </button>
        </c:if>
        <%--<input class="form-element" type="button" name="selectAll" id="selectAll" value="Select All"
               onclick="toggleSelect('jobID'); return false"/>--%>
        <button class="button" type="button" class="form-element" name="selectAll" id="selectAll" onclick="toggleSelect('jobID'); return false">
          Select All
        </button>
      </c:if>
      <input type="hidden" name="ACTION" id="ACTION" value="delete" />
      <input type="hidden" name="ID" value=""/>
    </div>
  </form:form>
</div>