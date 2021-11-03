/**
 * Created by dev_aka on 4/4/2017.
 */

function format ( row, tr ) {
    var d = row.data();
    // `d` is the original data object for the row
    var jobId = getSelectedJobId(d[1]);
    var username = document.getElementById('username').value;
    var convGraylog = document.getElementById('convGraylog').value;
    var jobExecGraylog = document.getElementById('jobExecGraylog').value;
    //ajax call to get data by jobid
    $.ajax({
        async: false,
        type: "POST",
        url: 'workqueue/getJobDetails/'+jobId,
        contentType : 'application/json; charset=utf-8',
        success: function (result) {
            var additionalInfo = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
            var jobEntry = result.jobEntry;
            var jobHistoryEntries = result.jobHistoryEntries;
            jobHistoryEntries.forEach(function(entry) {
                //Convert dateAdded from milliseconds to date
                var dateModified = new Date(entry.dateAdded).toUTCString();
                additionalInfo = additionalInfo.concat('<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">' +
                    '<tr>'+
                    '<td>Job status</td>'+
                    '<td>'+entry.status+ ' ( ' + entry.fullStatusName + ' ) ' + '</td>'+
                    '</tr>'+
                    '<tr>'+
                    '<td>Date that status was modified:</td>'+
                    '<td>'+dateModified+'</td>'+
                    '</tr>'+
                    '<tr>'+
                    '<td>Job Executor Name:</td>'+
                    '<td>'+entry.jobExecutorName+'</td>'+
                    '</tr>'+
                    '</table>'
                );
                return additionalInfo;
            });
            additionalInfo = additionalInfo.concat('</table>');
            if (username) {
                additionalInfo = additionalInfo.concat('<div>'+'<a href="'+convGraylog + jobId + '&from=' + jobEntry.fromDate + '.000Z' + '&to=' + jobEntry.toDate + '.000Z' +'">Converters graylog</a>'+
                    ' (Display Graylog Results for Converters for dates: '+ jobEntry.fromDate + ' to ' + jobEntry.toDate + ')' +'</div>' + '<br>' +
                    '<div>'+'<a href="'+jobExecGraylog + jobId + '&from=' + jobEntry.fromDate + '.000Z' + '&to=' + jobEntry.toDate + '.000Z' +'">JobExecutor graylog</a>' +
                    ' (Display Graylog Results for JobExecutor for dates: '+ jobEntry.fromDate + ' to ' + jobEntry.toDate + ')' +'</div>');
            }
            //show the row
            row.child(additionalInfo).show();
            tr.addClass('shown');
        },
        error: function () {
            alert('An error occurred.');
        }
    });

}

function getSelectedJobId(label){
    //label will be sth like <label for=\"job_1\">1</label>
    var regex =  /<label for(.*)\">/;
    var jobId = label.replace(regex,"").replace("</label>","");
    return jobId;
}

$(document).ready(function() {

    const LOCAL_STORAGE_KEY = 'workqueueFilter';

    var filter = {
        received : { selected: true , text: 'JOB'},
        processing: { selected: true , text: 'PROCESSING'},
        ready: { selected: true , text: 'READY'},
        error: { selected: true , text: 'FATAL'}
    };

    var initFilters = function () {
        if ( sessionStorage[LOCAL_STORAGE_KEY] ) {
            try {
                var tmp = JSON.parse(sessionStorage[LOCAL_STORAGE_KEY]);
                for (var i in tmp) {
                    if (!tmp[i].selected)
                        $("#" + i).click();
                }
            } catch (e) {
                sessionStorage[LOCAL_STORAGE_KEY] = null;
            }
        }
    };

    // initialize the Datatable
    var table = $('#workqueue_table').DataTable( {
        dom: 'Bfrtip',
        paging:   false,
        ordering: true,
        info:     true,
        searching: true,
        export: true,
        buttons: [
            {
                extend: 'csv',
                text: 'Export to CSV',
                customize: function (csv) {
                    var csvRows = csv.split('\n');
                    var csvColumns = csv.split(';');
                    csvColumns[0] = 'Job ID';
                    csvColumns[1] = 'Document URL';
                    csvColumns[2] = 'XQuery script';
                    csvColumns[3] = 'Job Result';
                    csvColumns[4] = 'Status';
                    csvColumns[5] = 'Started at';
                    csvColumns[6] = 'Instance';
                    csvColumns[7] = 'Duration';
                    csvRows[0] = csvColumns.join(';');
                    return csvRows.join('\n');
                },
                exportOptions: {
                    columns: ':gt(0)'
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

    // registering dropdown listeners
    $('.dropdown-container')
        .on('click', '.dropdown-button', function(e) {
            $('.dropdown-content').toggle();
        })

        .on('click', function (e) {
            e.stopPropagation();
        })

        .on('change', '[type="checkbox"]', function(e) {

            var status = this.name;
            var temp = [];

            filter[status].selected = ! filter[status].selected;

            for ( var i in filter){
                if (filter[i].selected) {
                    temp.push(filter[i].text)
                }
            }

            if ( temp.length === 4 || temp.length === 0){
                $('.fa-filter').css({ 'color': '#5f646f' })
                table.column( 5 ).search ( '' ) . draw ()
            }
            else{
                $('.fa-filter').css({ 'color': 'blue' });
                table.column( 5 ).search ( '(' + temp.join('|') + ')' , true ) . draw ();
            }

            sessionStorage[LOCAL_STORAGE_KEY] = JSON.stringify(filter);

            e.stopPropagation();

        });

    /* on outside clicks hide the dropdown */
    $(document).click(function(){
        $('.dropdown-content').hide();
    });

    initFilters(); // after having registered the listeners


    $('#workqueue_table tbody').on('click', 'td.details-control', function () {
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
