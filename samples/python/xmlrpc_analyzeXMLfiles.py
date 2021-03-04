#   Script for testing XMLCONV Conversion Service
#   The script is converting Excel file to XML.
#
#   Author: VLF, Vskiadas


import xmlrpc.client

import sys

server = xmlrpc.client.ServerProxy( sys.argv[1])
#server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

try:
    # json object to be send
    print("Printing arguments")
    print(sys.argv)
    print("End of Printing arguments")
   # param1 = sys.argv[2]
   # print(param1)

    method_result = server.XQueryService.analyzeXMLFiles({'http://rod.eionet.europa.eu/obligations/788': ['https://cdrtest.eionet.europa.eu/ro/colxmes8w/envx7jwya/xml']})

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
