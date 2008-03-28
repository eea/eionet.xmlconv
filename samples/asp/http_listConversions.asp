<%'/
'	ASP page for testing XMLCONV Conversion Service methods.
'
'	Author Enriko Käsper, TietoEnator
'/ 

	Dim server_url
	Dim xmlhttp, getUrl, method_path, convert_ID, result_type, description

	server_url = "http://80.235.29.171:8080/xmlconv/api"
	method_path = "/listConversions"
	schema = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd"
	
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
			convert_ID = x.getElementsByTagName("convert_id").item(0).text
			result_type = x.getElementsByTagName("result_type").item(0).text
			description = x.getElementsByTagName("description").item(0).text

			Response.write Convert_ID & ","
			Response.write result_type & ","
			Response.write description & ","
		Next
		Set objNodeList = nothing
		set xmlhttp = nothing
	End If

	set xmlhttp = nothing
%>