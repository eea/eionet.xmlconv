$(document).ready(function() {

    var table = $('#scriptExecutionHistoryTable').DataTable( {
        paging:   false,
        ordering: true,
        info:     true,
        searching: false,
        "order": [[ 1, "asc" ]],
        "oLanguage": {
            "sInfo": '_TOTAL_ entries<span class="lvllbl"></span>',
            "sInfoEmpty": '0 entries',
        },
    } );

    var table = $('#scriptExecutionVersionTable').DataTable( {
        paging:   false,
        ordering: true,
        info:     true,
        searching: false,
        "order": [[ 1, "asc" ]],
        "oLanguage": {
            "sInfo": '_TOTAL_ entries<span class="lvllbl"></span>',
            "sInfoEmpty": '0 entries',
        },
    } );
} );

function exportScriptMetadataToCsv() {
    const scriptId = document.getElementById("scriptId").value;
    axios.post("/scripts/" + scriptId +"/queryMetadata/exportToCsv", {responseType: 'blob'})
        .then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", "queryMetadata.csv");
            document.body.appendChild(link);
            link.click();
        })
}

function exportScriptMetadataHistoryToCsv() {
    const scriptId = document.getElementById("scriptId").value;
    axios.post("/scripts/" + scriptId +"/queryMetadataHistory/exportToCsv", {responseType: 'blob'})
        .then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", "queryMetadataHistory.csv");
            document.body.appendChild(link);
            link.click();
        })
}

