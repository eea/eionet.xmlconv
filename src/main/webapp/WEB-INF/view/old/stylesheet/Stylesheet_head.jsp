<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript" charset="utf-8" src="<c:url value="/scripts/DataTables/media/js/jquery.js" />"></script>
<script type="text/javascript" charset="utf-8"
        src="<c:url value="/scripts/DataTables/media/js/jquery.dataTables.js" />"></script>

<style type="text/css" title="currentStyle">
  @import "<c:url value="/scripts/DataTables/media/css/demo_table.css" />";

  table.dataTable tr {
    background-color: white;
  }

  table.dataTable tr:nth-child(even) {
    background-color: #f6f6f6;
  }

  table.dataTable td.sorting_1 {
    background-color: #EAEBFF
  }

  table.dataTable tr:nth-child(even) td.sorting_1 {
    background-color: #f6f6f6;
  }

  table.dataTable thead th {
    border-bottom: 0px;
  }

</style>

<script type="text/javascript">
  $(document).ready(function () {
    $('#tbl_stylesheets').dataTable({
      "bPaginate": false,
      "aaSorting": []
      ,
      "aoColumnDefs": [
        <c:choose>
        <c:when test="${stylesheet.permissions == 'ssdPrm'}">
        {"bVisible": false, "aTargets": [5]}
        , {"iDataSort": 5, "aTargets": [4]}
        , {'bSortable': false, 'aTargets': [0]}
        </c:when>
        <c:otherwise>
        {"bVisible": false, "aTargets": [4]}
        , {"iDataSort": 4, "aTargets": [3]}
        </c:otherwise>
        </c:choose>
      ]
    });
  });
</script>
