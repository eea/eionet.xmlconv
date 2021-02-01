(function(document, $){
    $(document).ready(function() {
        $('.statusHelp').bind("click", function(event) {
            event.preventDefault();
            // removes the <div> element with id="modalPopup" if it already exists
            $('#modalPopup').remove();
            // create <div> element with id="modalPopup"
            $('body').append('<div id="modalPopup">');
            // load the content from file qaScriptsStatuses into the modal div with ID="modalPopup"
            $('#modalPopup').load("/resources/qaScriptsStatuses", function(){
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


