$(document).ready(function() {
    $('#selMarkedHeavyReason').change(function(){
        var divID = $(this).children('option:selected').attr('id');
        if(divID == 'other'){
            $('#markedHeavyOtherReason').show();
        }else{
            $('#markedHeavyOtherReason').hide();
            $("#markedHeavyOtherReason").val('');
        }
    });
});

function hideDropdown(){
    $('#markedHeavyReason').hide();
}

function showDropdown(){
    $('#markedHeavyReason').show();
}