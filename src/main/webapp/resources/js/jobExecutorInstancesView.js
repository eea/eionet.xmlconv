$(document).ready(function() {
    // initialize the Datatable
    var table = $('#job_executor_instances_table').DataTable( {
        paging:   false,
        ordering: true,
        info:     true,
        searching: true,

        bAutoWidth: false,
        "order": [[ 1, "asc" ]],
        "oLanguage": {
            "sInfo": '_TOTAL_ entries<span class="lvllbl"></span>',
            "sInfoEmpty": '0 entries',
        },
    } );

    $('#job_executor_instances_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = table.row( tr );

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
        }
        else {
            // Call function to fill the table and show it
            format(row, tr);
        }
    } );
} );

function format ( row, tr ) {
    var d = row.data();
    // `d` is the original data object for the row
    var containerId = d[0];
    //ajax call to get data by containerId
    $.ajax({
        async: false,
        type: "POST",
        url: 'jobExecutorInstancesView/getJobExecutorDetails/'+containerId,
        contentType : 'application/json; charset=utf-8',
        success: function (result) {
            var additionalInfo = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
            result.forEach(function(entry) {
                //Convert dateAdded from milliseconds to date
                var dateAdded = new Date(entry.dateAdded).toUTCString();
                additionalInfo = additionalInfo.concat('<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
                    '<tr>'+
                    '<td>Job status</td>'+
                    '<td>')
                if(entry.status == 0){
                    additionalInfo = additionalInfo.concat('JOB ' + d[3] + ' RECEIVED BY WORKER');
                }
                else if (entry.status == 1){
                    additionalInfo = additionalInfo.concat('WORKER IS READY TO RECEIVE A JOB');
                }
                else if (entry.status == 2){
                    additionalInfo = additionalInfo.concat('WORKER FAILED');
                }
                else if (entry.status == 12){
                    additionalInfo = additionalInfo.concat('FME JOB ID WAS RECEIVED');
                }
                else{
                    additionalInfo = additionalInfo.concat('UNKNOWN STATUS: ' + entry.status);
                }

                additionalInfo = additionalInfo.concat('</td>'+
                    '</tr>'+
                    '<tr>'+
                    '<td>Timestamp:</td>'+
                    '<td>'+dateAdded+'</td>'+
                    '</tr>'+
                    '</table>'
                );
                return additionalInfo;
            });

            additionalInfo = additionalInfo.concat('</table>');
            //show the row
            row.child(additionalInfo).show();
            tr.addClass('shown');
        },
        error: function () {
            alert('An error occurred.');
        }
    });

}