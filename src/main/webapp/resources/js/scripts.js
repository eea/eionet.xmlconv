$(document).ready(function() {
    $('#selMarkedHeavyReason').change(function(){
        var divID = $(this).children('option:selected').attr('id');
        if(divID == 'other'){
            $('#markedHeavyOtherReason').css('display', 'block');
        }else{
            $('#markedHeavyOtherReason').hide();
            $("#markedHeavyOtherReasonTxt").val('');
        }
    });
    $('#selScriptType').change(function () {
        var value = $(this).children('option:selected').attr('value');
        if (value == 'fme') {
            $('#scriptRulesSection').hide();
            $('#heavyLightSection').hide();
        } else {
            $('#scriptRulesSection').show();
            $('#heavyLightSection').show();
        }
    });
});

function hideDropdown(){
    $('#markedHeavyReason').hide();
    $('#markedHeavyOtherReason').hide();
    $('#selMarkedHeavyReason').val('Long running script')
    $("#markedHeavyOtherReasonTxt").val('');
    $('#scriptRulesSection').show();
}

function showDropdown(){
    $('#markedHeavyReason').css('display', 'block');
    $('#scriptRulesSection').hide();
}
