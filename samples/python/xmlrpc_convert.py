#   Script for testing XMLCONV Conversion Service
#   convert XML
#
#   Author: Enriko Kasper

import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server = xmlrpclib.Server(server_url)
try:
	param1  = "https://svn.eionet.europa.eu/repositories/Reportnet/Dataflows/HabitatsDirectiveArticle17/xmlfiles/general-instancefile.xml"
	#convert_id parameter
	param2="26"

	method_result=server.ConversionService.convert(param1,param2)

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
