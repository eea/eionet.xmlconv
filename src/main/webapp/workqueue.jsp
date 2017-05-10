<%@ page import="eionet.gdem.Constants, eionet.gdem.services.GDEMServices, java.io.File"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/eurodyn.tld" prefix="ed" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
%>

<ed:breadcrumbs-push label="Workqueue" level="1" />
<tiles:insertDefinition name="TmpHeader">
    <tiles:putAttribute name="title" value="QA Jobs"/>
</tiles:insertDefinition>


<%--<%@ include file="menu.jsp" %>--%>

<link rel="stylesheet" type="text/css" href="<c:url value="/scripts/DataTables/media/css/jquery.dataTables.min.css" />"></link>
<link rel="stylesheet" type="text/css" href="<c:url value="/css/workqueue.css" />"></link>
<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css"></link>

<script type="text/javascript" src="<c:url value="/scripts/jquery-1.9.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/scripts/DataTables/media/js/jquery.dataTables.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/scripts/workqueue.js" />"></script>

<script type="text/javascript">
// <![CDATA[

var elementName="jobID";
isSelected = false;

function toggleSelect() {
  elems = document.getElementsByTagName("input");

  if (isSelected == false) {
    for (i = 0; i < elems.length; i++)
     if (elems[i].value && elementName==elems[i].name) {
      elems[i].checked = true ;
      }
      isSelected = true;
      document.getElementById("selectAll").value = "Deselect All";
      return isSelected;
  }
  else {
    for (i = 0; i < elems.length; i++)
      elems[i].checked = false ;
      isSelected = false;
      document.getElementById("selectAll").value = "Select All";
      return isSelected;
  }
}

function countSelected() {
    var j = 0;
    elems = document.getElementsByTagName("input");
    for (i = 0; i < elems.length; i++)
     if (elems[i].checked == true && elementName==elems[i].name) {
      j++;
    }
    return j;
}
function doDelete(){
    if(countSelected()==0){
        alert('No jobs selected!');
        return false;
    }
    if (!confirm('Are you sure you want to delete the selected jobs?'))
        return false;

    document.getElementById('ACTION').value='<%=Constants.WQ_DEL_ACTION%>';
    document.getElementById('jobs').submit();
}
function doRestart(){
    if(countSelected()==0){
        alert('No jobs selected!');
        return false;
    }
    document.getElementById('ACTION').value='<%=Constants.WQ_RESTART_ACTION%>';
    document.getElementById('jobs').submit();
}
// ]]>
</script>



        <%
            boolean wqdPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "d");
            boolean wquPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "u");
            boolean wqvPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "v");
            boolean logvPrm = user!=null && SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_LOGFILE_PATH, "v");

            String[][] list = null;
            try{
            eionet.gdem.services.db.dao.IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();
            list = jobDao.getJobData();
            }catch (Exception e) {e.printStackTrace();}
            String tmpFolder = Constants.TMP_FOLDER;
            String queriesFolder = Constants.QUERIES_FOLDER;
        %>
        <h1>Jobs</h1>

        <% if (err!=null){
            %>
            <div class="error-msg"><%=err%></div>
        <%}    %>
        <p>Currently there are following jobs in the queue...</p>
        <%
            if (logvPrm){
        %>
        <p>
            <a href='log/xmlconv.log'>View log file</a>
        </p>
        <%
            }
        %>
        <div id="main_table">
        <form id="jobs" action="main" method="post">
            <table id="workqueue_table" class="datatable" >
                <col style="width:20px; text-align:right;">
                <col style="width:50px; text-align:right;"/>
                <col style="width:550px; text-align:right;"/>
                <col style="width:80px; text-align:right;"/>
                <col style="width:100px"/>
                <col style="width:100px"/>
                <col style="width:100px"/>
                <col style="width:120px"/>
                <thead>
                    <tr>
                        <th scope="col" class="scope-col"></th>
                        <th scope="col" class="scope-col">Job Id</th>
                        <th scope="col" class="scope-col">Document URL</th>
                        <th scope="col" class="scope-col">XQuery script</th>
                        <th scope="col" class="scope-col">Job Result</th>
                        <th scope="col" class="scope-col">
                            <div class="dropdown-container">
                                <div class="dropdown-button noselect">
                                    <div>
                                        <div class="dropdown-label">Status</div>
                                    </div>
                                    <i class="fa fa-filter"></i>
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
                    </tr>
                </thead>
                <tbody>
                <%
                eionet.gdem.services.db.dao.IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
                for (int i=0; i<list.length; i++){
                    String jobId = list[i][0];
                    String url = list[i][1];
                    String xqLongFileName = list[i][2];
                    String xqFile = list[i][2].substring(list[i][2].lastIndexOf(File.separatorChar)+1);
                    String resultFile = list[i][3].substring(list[i][3].lastIndexOf(File.separatorChar)+1);
                    int status = Integer.parseInt(list[i][4]);
                    String timeStamp = list[i][5];
                    String xqStringID = list[i][6];
                    String instance = list[i][7];

                      int xqID =0;
                      String scriptType = "";
                      try {
                        xqID=Integer.parseInt(xqStringID);
                        java.util.HashMap query = queryDao.getQueryInfo(xqStringID);
                        if (query != null) {
                            scriptType = (String)query.get("script_type");
                        }
                      } catch(NumberFormatException n) {
                        xqID = 0;
                      } catch (Exception e) {e.printStackTrace();}

                    String xqFileURL = "";
                    String xqText = "Show script";
                    if (xqID == Constants.JOB_VALIDATION) {
                        xqText = "Show XML Schema";
                        xqFileURL = xqLongFileName;
                    }
                    else if (xqID == Constants.JOB_FROMSTRING) {
                        xqFileURL = tmpFolder + xqFile;
                    }
                    else{
                        xqFileURL = queriesFolder + xqFile;
                    }


                    if (status==Constants.XQ_RECEIVED || status==Constants.XQ_DOWNLOADING_SRC || status==Constants.XQ_PROCESSING)
                        resultFile = null;

                    //TODO Status name, maybe better to move to some Java common class
                    String statusName="-- Unknown --";

                    if (status==Constants.XQ_RECEIVED)
                        statusName="JOB RECEIVED";
                    if (status==Constants.XQ_DOWNLOADING_SRC)
                        statusName="DOWNLOADING SOURCE";
                    if (status==Constants.XQ_PROCESSING)
                        statusName="PROCESSING";
                    if (status==Constants.XQ_READY)
                        statusName="READY";
                    if (status==Constants.XQ_FATAL_ERR)
                        statusName="FATAL ERROR";
                    if (status==Constants.XQ_LIGHT_ERR)
                        statusName="RECOVERABLE ERROR";


                    if(url.indexOf(Constants.GETSOURCE_URL)>0 && url.indexOf(Constants.SOURCE_URL_PARAM)>0) {
                        int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
                        url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
                    }

                    String urlName = url;

        %>
                    <tr <% if (i % 2 != 0) %>class="zebraeven"<% else %>class="zebraodd"<%;%>>
                            <%
                            if (wqdPrm || wquPrm) {
                            %>
                                <td>
                                    <input type="checkbox" name="jobID" id="job_<%=jobId%>" value="<%=jobId%>"/>
                                </td>
                                <td>
                                    <label for="job_<%=jobId%>"><%=jobId%></label>
                                </td>
                            <%} else {%>
                                <td/>
                                <td>
                                    <%=jobId%>
                                </td>
                            <%}%>
                        <td style="word-wrap:break-word;">
                            <a href="<%=url%>" rel="nofollow"><%=urlName%></a>
                        </td>
                      <td>
                            <%if (!eionet.gdem.qa.XQScript.SCRIPT_LANG_FME.equals(scriptType)) {%>
                                <a href="<%=xqFileURL%>" rel="nofollow"><%=xqText%></a>
                            <%}else{%>
                                FME
                            <%}%>
                        </td>
                        <td>
                            <% if (resultFile != null) {
                                if (wqvPrm) {%>
                                    <a href="<%=tmpFolder + resultFile%>" rel="nofollow">Job result</a>
                                <% } else { %>
                                    <div title="Log in to see job result">Job result</div>
                                <%
                                }
                            } else {
                                out.println("*** Not ready ***");
                            } %>
                        </td>
                        <td>
                            <%=statusName%>
                        </td>
                        <td>
                            <%=timeStamp%>
                        </td>
                        <td>
                            <% if (wqvPrm) { %>
                                     <%=instance%>
                            <%     } else { %>
                                    <div title="Log in to see system info">-</div>
                            <% } %>
                        </td>
                    </tr>
                    <%
                   }
               %>
                </tbody>
             </table>
            <div id="hidden_elements">
            <%
            if (wqdPrm || wquPrm) {%>
                <% if (wqdPrm) {%>
                    <input type="button" value="Delete" onclick="return doDelete();"/>
                <%}%>
                <% if (wquPrm) {%>
                    <input type="button" value="Restart" onclick="return doRestart();"/>
                <%}%>
                <input class="form-element" type="button" name="selectAll" id="selectAll" value="Select All" onclick="toggleSelect('jobID'); return false"/>
            <%}%>
                <input type="hidden" name="ACTION" id="ACTION" value="<%=Constants.WQ_DEL_ACTION%>"/>
                <input type="hidden" name="ID" value="" />
            </div>
        </form>
    </div>
<tiles:insertDefinition name="TmpFooter"/>
