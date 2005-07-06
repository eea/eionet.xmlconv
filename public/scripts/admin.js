function stylesheetDelete(title) {
	if(confirm('Do you want to delete stylesheet "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}


function schemaDelete(title) {
	if(confirm('Do you want to delete schema "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}
function elementDelete(title) {
	if(confirm('Do you want to delete root element "' + title + '"')){
		return true;
	}
	else {
		return false;
	}
}

function submitAction(act)
{
	document.forms[0].action = act;
	document.forms[0].submit();
	return true;
}

