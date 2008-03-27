#   Script for testing XMLCONV Conversion Service
#   getXMLSchemas
#
#   Author: Enriko Kasper

import xmlrpclib

#server_url="http://converters.eionet.eu.int/RpcRouter"
server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server = xmlrpclib.Server(server_url)
try:

	method_result=server.ConversionService.getXMLSchemas()

except xmlrpclib.ProtocolError, p:
         err_code=p.errcode    #handle error according to error code

print method_result
