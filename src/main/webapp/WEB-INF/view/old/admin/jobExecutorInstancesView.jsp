<%@include file="/WEB-INF/view/old/taglibs.jsp" %>

<ed:breadcrumbs-push label="Workqueue" level="1"/>

<link rel="stylesheet" type="text/css" href="<c:url value='/static/js/DataTables/media/css/jquery.dataTables.min.css'/>"></link>
<link rel="stylesheet" type="text/css" href="<c:url value='/static/js/jobExecutorInstancesView.css'/>"></link>
<link rel="stylesheet" type="text/css" href="<c:url value='/static/webjars/font-awesome/web-fonts-with-css/css/fontawesome-all.css'/>"></link>

<link rel="stylesheet" type="text/css" href="<c:url value='/static/js/workqueue.css'/>"></link>

<script type="text/javascript" src="<c:url value='/static/js/DataTables/media/js/jquery.dataTables.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/static/js/jobExecutorInstancesView.js'/>"></script>

<div style="width:100%;">
    <tiles:insertDefinition name="AdminTabs">
        <tiles:putAttribute name="selectedTab" value="jobExecutorInstancesView"/>
    </tiles:insertDefinition>

    <ed:breadcrumbs-push label="View Job Executor Instances" level="1"/>

    <h1>Job Executor Instances</h1>

    <p>Currently there are following Job Executor instances in rancher:</p>
    <div id="main_table">
        <table id="job_executor_instances_table" class="datatable results" width="100%">
            <thead>
            <tr>
                <th scope="col" class="scope-col details-control">Container Id</th>
                <th scope="col" class="scope-col">Name</th>
                <th scope="col" class="scope-col">Status</th>
                <th scope="col" class="scope-col">Job Id</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach items="${instances}" varStatus="i" var="instance">
                <tr class="${i.index % 2 == 1 ? 'zebraodd' : 'zebraeven'}">
                    <td class="details-control" id="selectableContainerId">
                        ${instance.containerId}
                    </td>
                    <td>
                        ${instance.name}
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${instance.status == 0}">
                                <p>JOB RECEIVED BY WORKER<p>
                            </c:when>
                            <c:when test="${instance.status == 1}">
                                <p>WORKER IS READY TO RECEIVE A JOB<p>
                            </c:when>
                            <c:when test="${instance.status == 2}">
                                <p>WORKER FAILED<p>
                            </c:when>
                            <c:otherwise>
                                <p>UNKNOWN STATUS: ${instance.status} <p>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        ${instance.jobId}
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>