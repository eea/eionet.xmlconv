#   Script for testing XMLCONV Conversion Service
#   getXMLSchemas
#
#   Author: VLF

import xmlrpc.client
import sys

server = xmlrpc.client.ServerProxy(sys.argv[1])

try:
    method_result = server.ConversionService.getXMLSchemas()

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
