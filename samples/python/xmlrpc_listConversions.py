#   Script for testing XMLCONV Conversion Service
#   listConversions
#
#   Author: VLF

import xmlrpc.client

server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

try:
	# file parameter value encoded as Base64 byte array
	param1 = "http://biodiversity.eionet.europa.eu/schemas/dir9243eec/generalreport.xsd"

	method_result = server.ConversionService.listConversions(param1)

except xmlrpc.client.ProtocolError as err:
	print("A protocol error occurred")
	print("URL: %s" % err.url)
	print("HTTP/HTTPS headers: %s" % err.headers)
	print("Error code: %d" % err.errcode)
	print("Error message: %s" % err.errmsg)

print(method_result)
