#   Script for testing XMLCONV Conversion Service
#   getXMLSchemas
#
#   Author: Enriko Kasper

import xmlrpc.client

#server_url="http://converters.eionet.eu.int/RpcRouter"
server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

try:

    method_result=server.ConversionService.getXMLSchemas()

except xmlrpclib.ProtocolError, p:
    err_code=p.errcode    #handle error according to error code

print method_result
