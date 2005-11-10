function stylesheetDelete(title) {
	if(confirm('Are you sure you want to delete stylesheet "' + title + '"')){
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
function elementDelete(title) {
	if(confirm('Are you sure you want to delete root element "' + title + '"')){
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

function setSchema()
{
	document.forms[0].schema.value = document.forms[0].xmlSchema.options[document.forms[0].xmlSchema.selectedIndex].value;

	return true;
}
