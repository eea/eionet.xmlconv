#   Script for testing XMLCONV Conversion Service
#   convert XML
#
#   Author: VLF

import xmlrpc.client

# server_url="http://converters.eionet.eu.int/RpcRouter"
server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

try:
    param1 = "https://svn.eionet.europa.eu/repositories/Reportnet/Dataflows/HabitatsDirectiveArticle17/xmlfiles/general-instancefile.xml"
    # convert_id parameter
    param2 = "26"

    method_result = server.ConversionService.convert(param1, param2)

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
