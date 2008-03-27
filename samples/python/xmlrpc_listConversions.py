#   Script for testing XMLCONV Conversion Service
#   listConversions
#
#   Author: Enriko Kasper

import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server = xmlrpclib.Server(server_url)
try:
	#file parameter value encoded as Base64 byte array
	param1 = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd"

	method_result=server.ConversionService.listConversions(param1)

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
