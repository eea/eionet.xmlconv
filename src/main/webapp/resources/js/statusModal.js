(function(document, $){
    $(document).ready(function() {
        $('.statusHelp').bind("click", function(event) {
            event.preventDefault();
            // removes the <div> element with id="modalPopup" if it already exists
            $('#modalPopup').remove();
            // create <div> element with id="modalPopup"
            $('body').append('<div id="modalPopup">');
            // load the content from file statuses into the modal div with ID="modalPopup"
            $('#modalPopup').load("/resources/statuses", function(){
                $('#modalPopup div').removeAttr('id');
            });
            // popup the dialog window with predifined dimensions and functionality concerning the close event
            $('#modalPopup').dialog({
                position: { my: "center", at: "left" },
                title: "QA SCRIPTS STATUSES",
                width: 350,
                height: 510,
                // when dialog is closed <div> element will be removed from DOM
                close: function () {
                    $('#modalPopup').remove();
                }
            });
        });
    });
})(document, jQuery);


$('.statusHelp').click(function() {
    $('#modalPopup').load("/resources/statuses");
    $('#modalPopup').dialog('open');
});

$(document).ready(function() {
    jQuery("#dialog").dialog({
        autoOpen:false,
        modal: true,
        resizable: false,
        draggable: false,
        closeOnEscape: true,
        position: ['center',20],
        title: "Trazoo",
        open: function(){
            jQuery('.ui-widget-overlay').bind('click',function(){
                jQuery('#modalPopup').dialog('close');
            })
        }
    });
});