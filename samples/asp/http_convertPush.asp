<%
'/
'	ASP page for testing XMLCONV Conversion Service methods.
'
'	The script requires seed-general-report.xml and seed-general-report.zip 
'	files stored in the same directory	as asp file.
'
'	Author Enriko Käsper, TietoEnator
'/ 

	Dim server_url, method_path
	Dim xmlhttp, DataToSend, postUrl

	server_url = "http://80.235.29.171:8080/xmlconv/api"
	method_path = "/convert"
		
	convert_file="seed-general-report.xml"
	convert_id="26"

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

%>