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
        const scriptType = $(this).children('option:selected').attr('value');
        hideOrShowByScriptType(scriptType);
        const asynchronousExecution = $('input[name="asynchronousExecution"]:checked').val();
        setResultTypeOptions(scriptType, asynchronousExecution);
    });
    $('input[name="asynchronousExecution"]').change(function() {
        const scriptType = $('#selScriptType').val();
        setResultTypeOptions(scriptType, $(this).val());
    });
});

window.onload = function () {
    var scriptType =  $('#selScriptType').children('option:selected').attr('value');
    hideOrShowByScriptType(scriptType);
    var markedHeavy =  $('#heavy').attr('checked');
    if (markedHeavy=='checked') {
        $('#markedHeavyReason').css('display', 'block');
        $('#scriptRulesSection').hide();
    }
};

function setResultTypeOptions(scriptType, asynchronousExecution) {
    if (scriptType === 'fme' && asynchronousExecution === "true") {
        $('#selContentType').val("ZIP");
        $('#selContentType option:not([value="ZIP"])').prop('disabled', true);
    } else {
        $('#selContentType option').prop('disabled', false);
    }
}

function hideOrShowByScriptType(value) {
    if (value == 'fme') {
        $('#scriptRulesSection').hide();
        $('#heavyLightSection').hide();
    } else {
        $('#scriptRulesSection').show();
        $('#heavyLightSection').show();
    }
}

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
