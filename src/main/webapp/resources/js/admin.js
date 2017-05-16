function stylesheetDelete(title) {
    if(confirm('Are you sure you want to delete stylesheet "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}
function qaScriptDelete(title) {
    if(confirm('Are you sure you want to delete QA script "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}


function schemaDelete(title) {
    if(confirm('Are you sure you want to delete schema "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}
function xmlfileDelete(title) {
    if(confirm('Are you sure you want to delete XML file "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}

function elementDelete(title) {
    if(confirm('Are you sure you want to delete root element "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}

function submitAction(formIdx, act)
{
    document.forms[formIdx].action = act;
    document.forms[formIdx].submit();
    return true;
}

function setSchema()
{
    document.forms[0].schema.value = document.forms[0].xmlSchema.options[document.forms[0].xmlSchema.selectedIndex].value;

    return true;
}

function openWindow(theURL, winName) {
   var h, w;
   h = screen.height;
   w = screen.width;
   var l, t;
   l = parseInt((w - 795)/2);
   t = parseInt((h - 550)/2);

   WinId = window.open(theURL, 'HelpWindow', 'toolbar=no,menubar=no,location=no,status=no,scrollbars=yes,resizable=yes,width=795,height=550,left=' + l + ',top=' + t);
   WinId.focus();
   WinId.document.location =theURL;
}

function hostDelete(title) {
    if(confirm('Are you sure you want to delete host "' + title + '"')){
        return true;
    }
    else {
        return false;
    }
}

