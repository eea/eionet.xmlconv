/**
 * Created by dev_aka on 4/4/2017.
 */
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

} );
