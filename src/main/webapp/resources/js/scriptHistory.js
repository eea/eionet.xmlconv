$(document).ready(function() {

    var table = $('#scriptExecutionHistoryTable').DataTable( {
        paging:   false,
        ordering: true,
        info:     true,
        searching: false,
        dom: 'Bfrtip',
        buttons: [
            {
                extend: 'csv',
                text: 'Export to CSV',
                customize: function (csv) {
                    var csvRows = csv.split('\n');
                    var csvColumns = csv.split(';');
                    csvColumns[0] = 'File Name';
                    csvColumns[1] = 'Script Type';
                    csvColumns[2] = 'Duration';
                    csvColumns[3] = 'Was Heavy';
                    csvColumns[4] = 'Job Status';
                    csvColumns[5] = 'Version';
                    csvRows[0] = csvColumns.join(';');
                    return csvRows.join('\n');
                }
            }
        ],
        bAutoWidth: false,
        "order": [[ 1, "asc" ]],
        "oLanguage": {
            "sInfo": '_TOTAL_ entries<span class="lvllbl"></span>',
            "sInfoEmpty": '0 entries',
        },
    } );
} );
