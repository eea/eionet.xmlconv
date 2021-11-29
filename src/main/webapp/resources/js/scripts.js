$(document).ready(function() {
    $('#selMarkedHeavyReason').change(function(){
        var divID = $(this).children('option:selected').attr('id');
        if(divID == 'other'){
            $('#markedHeavyOtherReason').show();
        }else{
            $('#markedHeavyOtherReason').hide();
            $("#markedHeavyOtherReasonTxt").val('');
        }
    });
});

function hideDropdown(){
    $('#markedHeavyReason').hide();
    $('#markedHeavyOtherReason').hide();
    $('#selMarkedHeavyReason').val('Long running script')
    $("#markedHeavyOtherReasonTxt").val('');
}

function showDropdown(){
    $('#markedHeavyReason').show();
}