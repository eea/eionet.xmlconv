<%
'/
'	ASP page for testing XMLCONV Conversion Service methods.
'
'	The script requires seed-general-report.xml and seed-general-report.zip 
'	files stored in the same directory	as php file.
'
'	Author Enriko Käsper, TietoEnator
'/ 

Dim server_url
Dim schemas, conversions, convert_file, convert_id, schema, action, xmlfiles

server_url = "http://80.235.29.171:8080/xmlconv/api"
'server_url = "http://localhost:8080/xmlconv/api"

	'read SESSION parameters
	schemas = Session("schemas")
	convert_url = Session("convert_url")
	convert_file = Session("convert_file")
	convert_id = Session("convert_id")
	schema = Session("schema")
	xmlfiles = Session("xmlfiles")

	If IsArray(xmlfiles)=False Then
		xmlfiles=loadXMLFiles()
		session("xmlfiles")=xmlfiles
	End If					

	'read request parameters and store these in the session
	action = Request("action")
	If action = Null Then action=""

	If action="convertPush" Then
		convert_file = Request("convert_file")
		convert_id = Request("convert_id")
		
		convertPush convert_file, convert_id
		
		Session("convert_file")=convert_file
		Session("convert_id")=convert_id
		'return;
	ElseIf action = "convert" Then
		convert_url = Request("convert_url")
		convert_id = Request("convert_id")

		convert convert_url, convert_id
		
		Session("convert_url") = convert_url
		Session("convert_id") = convert_id

	ElseIf action = "getXMLSchemas" Then
		schemas = getXMLSchemas()
		Session("schemas") = schemas
	ElseIf action = "listConversions" Then
		schema = Request("schema")
		conversions = listConversions(schema)
		
		'Session("conversions") = conversions
		Session("schema") = schema
	ElseIf action="clear" Then
		Session.Abandon
	End If
%>
<%
'/**
'*	Function calls convrtPush method
'*/
	Sub convertPush(convert_file,convert_id)
		Dim xmlhttp, DataToSend, postUrl
		Const boundary = "---------------------------0123456789012"

		method_path = "/convertPush"
		'//NB the source file has to be in the same directory or modify the path info in $dir variable

		postUrl = server_url & method_path

		Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
		xmlhttp.Open "POST", postUrl, False
		xmlhttp.setRequestHeader "Content-Type", "multipart/form-data; boundary=" + boundary
	    FileContents = GetFile(convert_file)
		formData = BuildFormData(FileContents, boundary, convert_file, "convert_file", convert_id)
		xmlhttp.Send formData

		Response.ContentType =xmlhttp.getResponseHeader("Content-type")
	    if xmlhttp.status = 200 Then 
			Response.AddHeader "Content-Disposition", xmlhttp.getResponseHeader("Content-Disposition") 
		End If
		Response.BinaryWrite  xmlhttp.responseBody
	    Response.End

		set xmlhttp = nothing
	End Sub
	'Helper function returns file contents As a binary data
	Function GetFile(FileName)
	  Dim Stream: Set Stream = CreateObject("ADODB.Stream")

		set fs=Server.CreateObject("Scripting.FileSystemObject")
		Set rs = fs.GetFile(Server.MapPath(FileName))

	  Stream.Type = 1 'Binary
	  Stream.Open
	  Stream.LoadFromFile rs.ParentFolder & "\" & FileName
	  GetFile = Stream.Read
	  Stream.Close
		set fs=nothing
		Set rs = nothing
	End Function

	'Build multipart/form-data document with file contents And header info
	Function BuildFormData(FileContents, Boundary, FileName, FieldName, convert_id)
	  Dim FormData, Pre, Po, convertIdParam
	  Const ContentType = "application/upload"
  
	  convertIdParam = "--" & Boundary & vbCrLf & _
                              "Content-Disposition: form-data; name=""convert_id""" & vbCrLf & vbCrLf & _
                              convert_id & vbCrLf
	  'The two parts around file contents In the multipart-form data.
	  Pre = "--" + Boundary + vbCrLf + mpFields(FieldName, FileName, ContentType)
	  Po = vbCrLf + "--" + Boundary + "--" + vbCrLf
  
	  'Build form data using recordset binary field
	  Const adLongVarBinary = 205
	  Dim RS: Set RS = CreateObject("ADODB.Recordset")
	  RS.Fields.Append "b", adLongVarBinary, Len(Pre) + LenB(FileContents) + Len(Po)
	  RS.Open
	  RS.AddNew
		Dim LenData

		'Convert convertIdParam string value To a binary data
		LenData = Len(convertIdParam)
	    RS("b").AppendChunk (StringToMB(convertIdParam) & ChrB(0))
		convertIdParam = RS("b").GetChunk(LenData)
	    RS("b") = ""
	
		'Convert Pre string value To a binary data
	    LenData = Len(Pre)
	    RS("b").AppendChunk (StringToMB(Pre) & ChrB(0))
	    Pre = RS("b").GetChunk(LenData)
	    RS("b") = ""
    
		'Convert Po string value To a binary data
	    LenData = Len(Po)
	    RS("b").AppendChunk (StringToMB(Po) & ChrB(0))
	    Po = RS("b").GetChunk(LenData)
	    RS("b") = ""
    
		'Join convertIdParam + Pre + FileContents + Po binary data
	    RS("b").AppendChunk (convertIdParam)
	    RS("b").AppendChunk (Pre)
		RS("b").AppendChunk (FileContents)
	    RS("b").AppendChunk (Po)
	  RS.Update
	  FormData = RS("b")
	  RS.Close
	  BuildFormData = FormData
	End Function
	'Helper - Infrormations In form field header.
	Function mpFields(FieldName, FileName, ContentType)
	  Dim MPTemplate 'template For multipart header
	  MPTemplate = "Content-Disposition: form-data; name=""{field}"";" + _
	   " filename=""{file}""" + vbCrLf + _
	   "Content-Type: {ct}" + vbCrLf + vbCrLf
	  Dim Out
	  Out = Replace(MPTemplate, "{field}", FieldName)
	  Out = Replace(Out, "{file}", FileName)
	  mpFields = Replace(Out, "{ct}", ContentType)
	End Function
	'Helper - Converts OLE string To multibyte string
	Function StringToMB(S)
	  Dim I, B
	  For I = 1 To Len(S)
		B = B & ChrB(Asc(Mid(S, I, 1)))
	  Next
	  StringToMB = B
	End Function
'/**
'*	Function calls convert method
'*/
	Sub convert(convert_url, convert_id)
		Dim xmlhttp, DataToSend, postUrl, headers

		method_path = "/convert"
		
		postData="url=" & convert_url
		postData = postData & "&convert_id=" & convert_id


		postUrl = server_url & method_path

		Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
		xmlhttp.Open "POST", postUrl, False
		xmlhttp.setRequestHeader "Content-Type", "application/x-www-form-urlencoded"
		xmlhttp.Send postData

								
		Response.ContentType =xmlhttp.getResponseHeader("Content-type")

	    if xmlhttp.status = 200 Then 
			Response.AddHeader "Content-Disposition", xmlhttp.getResponseHeader("Content-Disposition") 
		End If
		Response.BinaryWrite  xmlhttp.responseBody
		Response.End

		set xmlhttp = nothing
	End Sub
'/**
'*	Function calls getXMLSchemas method
'*/
	Function getXMLSchemas()
		Dim xmlhttp, getUrl, schemas()

		method_path = "/getXMLSchemas"
		
		getUrl = server_url & method_path

		Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
		xmlhttp.Open "GET", getUrl, False
		xmlhttp.Send

	    if xmlhttp.status = 200 Then 
	 
          sXML = xmlhttp.responseText   ' Retrieve from server.

		  Set oXML = CreateObject("MSXML2.DOMDocument.3.0")
		  oXML.loadXML(sXML)
		  Set objNodeList = oXML.getElementsByTagName("schema")
			for each x in objNodeList
			  ReDim Preserve schemas(i)
			  schemas(i)=x.text
			  i=i+1
			next
			Set objNodeList = nothing
			set xmlhttp = nothing
		End If

		set xmlhttp = nothing
		getXMLSchemas=schemas
	End Function

'/**
'*	Function calls listConversions method
'*/
	Function listConversions(schema)
		Dim xmlhttp, getUrl, conversions()

'Set conversions=Server.CreateObject("Scripting.Dictionary")
'		conversions.Add "m", k

		method_path = "/listConversions"
		
		getUrl = server_url & method_path & "?schema=" & schema

		Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP.3.0")
		xmlhttp.Open "GET", getUrl, False
		xmlhttp.Send

	    if xmlhttp.status = 200 Then 
	 
          sXML = xmlhttp.responseText   ' Retrieve from server.

		  Set oXML = CreateObject("MSXML2.DOMDocument.3.0")
		  oXML.loadXML(sXML)
		  Set objNodeList = oXML.getElementsByTagName("conversion")
			for each x in objNodeList
			  ReDim Preserve conversions(i)
			  conversions(i)=x.xml
			  i=i+1
			next
			Set objNodeList = nothing
			set xmlhttp = nothing
		End If

		set xmlhttp = nothing
		listConversions=conversions		   
	End Function
'/**
'*	Function reads xml file names from filesystem
'*/
	Function loadXMLFiles()
		
		dim fs,fo,x,xmlfiles(),i
		set fs=Server.CreateObject("Scripting.FileSystemObject")
		Set rs = fs.GetFile(Server.MapPath("testConversionService.asp"))
		set fo=fs.GetFolder(rs.ParentFolder)
		i=0
		for each x in fo.files
			if Right(x,3)="xml" or Right(x,3)="zip" Then
			  ReDim Preserve xmlfiles(i)
			  xmlfiles(i)=x.Name
			  i=i+1
			End If
		next
	
		set fo=nothing
		set rs=nothing
		set fs=nothing
		loadXMLFiles = xmlfiles
	End Function

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Test Conversion methods</title>
		<style type="text/css" media="screen">
		//<!--
			fieldset{
				margin: 2px;
			}
			legend{
				font-weight:bold;
				font-size: 12pt;
				color: black;
			}
			.row{
				display:block;
				width:100%;
			}
			label{
				width: 10em;
				background-color: #f0f0f0;
				vertical-align: top;
				margin-top: 5px;
				margin-right: 5px;
				float: left;
			}
			input[type="text"], input[type="file"]{
				width: 500px;
				margin-top: 5px;
			}
			input[type="radio"]{
				margin-top: 5px;
			}
			input[type="button"],input[type="submit"]{
				margin-top: 5px;
			}
			select{
				margin: 2px;
			}
			.radioLabel{
				width: 10em;
				background-color: white;
				vertical-align: top;
				margin: 3px;
				float: none;
			}
			.radioContainer{
				margin-top: 7px;
				margin-left: 30px;
				width:100%;
				display:block;
			}
		//-->
		</style>
		<script type="text/javascript">
		//<!--
		function getXMLSchemasXML(){
			document.location = document.getElementById("server").value +  "/getXMLSchemas";
		}
		function listConversionsXML(){
			document.location = document.getElementById("server").value +  "/listConversions?schema=" + document.getElementById("selectSchema").value;
		}
		function loadSchemas(){
			document.location = "testConversionService.php?action=getXMLSchemas";
		}
		function setConversionId(){
			var selectedConversion=document.getElementById("selectConversion").value;
			var inpObjects = document.getElementsByTagName("input");
			for (var i=0; i < inpObjects.length;i++){
				var name = inpObjects[i].name;
				if(name=="convert_id"){
					inpObjects[i].value=selectedConversion;
				}
			}
		}
		function init(){
			setConversionId();
		}
		window.onload = init;
		//-->
		</script>
	</head>
	<body>
		<h1>Test XMLCONV conversion methods - ASP</h1>
		<form id="listConversions" method="get" action="testConversionService.asp">
			<fieldset>
				<legend>1. "listConversions" method - search for available conversions</legend>
				<div class="row" style="display:none">
					<label for="server">Server URL:</label><input type="text" id="server" name="server" value="<%=server_url%>"/>
				</div>
				<div class="row">
					<label for="selectSchema">Select XML Schema:</label>
					<select id="selectSchema" name="schema">
						<%
						Dim selected
						If IsArray(schemas) then 
						for each item in schemas	
							If item=schema Then 
								selected="selected=""selected""" 
							Else selected=""
							End If
							%>
							<option <%=selected%> value="<%=item%>"><%=item%></option>
							<%
						next
						End If
						%>
					</select>
					<input type="submit" value="getXMLSchemas" name="action"/>
					<input type="button" onclick="getXMLSchemasXML()" value="show getXMLSchemas XML"/>
				</div>
				<div class="row">
					<label for="selectConversion">Select conversion:</label>
					<select id="selectConversion" name="conversionId" onchange="setConversionId()">
						<%
						If IsArray(conversions) then 
						for each item in conversions	
							Set oXML = CreateObject("MSXML2.DOMDocument.3.0")
							oXML.loadXML(item)
							convert_ID = oXML.getElementsByTagName("convert_id").item(0).text
							result_type = oXML.getElementsByTagName("result_type").item(0).text
							description = oXML.getElementsByTagName("description").item(0).text
							Set oXML=nothing
							%>
							<option value="<%=convert_ID%>"><%=result_type%> - <%=description%></option>
							<%
						Next
						End If
						 %>
					</select>
					<input type="submit" value="listConversions" name="action"/>
					<input type="button" onclick="listConversionsXML()" value="show listConversions XML"/>
				</div>
			</fieldset>
		</form>

		<form id="convert" method="get" action="testConversionService.asp">
			<fieldset>
				<legend>2. "convert" method - insert the URL of XML file and execute the conversion</legend>
				<label for="convert_url">URL of XML file:</label><input type="text" id="convert_url" name="convert_url" value="<%= convert_url%>"/>
				<br/>
				<label for="convert_id1">Conversion ID:</label><input type="text" id="convert_id1" name="convert_id" value="<%=convert_id%>"/>
				<br/>
				<input type="submit" value="convert" name="action"/>
			</fieldset>
		</form>

		<form id="convertPush" method="get" action="testConversionService.asp">
			<fieldset>
				<legend>3. "convertPush" method - select the XML (or zipped XML) file name stored in the server and execute the conversion</legend>
				<label>Select XML file:</label><br/>
				<div class="radioContainer">
				<%
				Dim checked
				If IsArray(xmlfiles) then 
				for each item in xmlfiles	
					If item=convert_file Then 
						checked="checked=""checked""" 
					Else checked=""
					End If
					%>
					<input type="radio" name="convert_file" id="<%=item%>" value="<%=item%>" <%=checked%>/><label class="radioLabel" for="<%=item%>"><%=item%></label><br/>
					<%
				next
				End If
				%>
				</div>
				<br/>
				<label for="convert_id2">Conversion ID:</label><input type="text" id="convert_id2" name="convert_id" value="<%=convert_id%>"/>
				<br/>
				<input type="submit" value="convertPush" name="action"/>
			</fieldset>
		</form>

		<form id="clear" method="get" action="testConversionService.asp">
			<fieldset>
				<input type="submit" value="clear" name="action"/>
			</fieldset>
		</form>
		<%=action%>
</body>
</html>
