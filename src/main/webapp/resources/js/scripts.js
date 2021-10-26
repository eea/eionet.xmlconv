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
    $('#markedHeavyOtherReason').hide();
    $('#markedHeavyOtherReason').val('Long running script')
}

function showDropdown(){
    $('#markedHeavyReason').show();
}