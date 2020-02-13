#   Script for testing XMLCONV Conversion Service
#   convert XML
#
#   Author: VLF

import xmlrpc.client
import sys
# server_url="http://converters.eionet.eu.int/RpcRouter"
server = xmlrpc.client.ServerProxy(sys.argv[1])

try:
    param1 = sys.argv[2]
    # convert_id parameter
    param2 = sys.argv[3]

    method_result = server.ConversionService.convert(param1, param2)

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
