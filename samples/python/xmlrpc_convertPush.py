#   Script for testing XMLCONV Conversion Service
#   convertPush
#
#   Author: VLF

import xmlrpc.client

# server_url="http://converters.eionet.eu.int/RpcRouter"
# server_url="http://80.235.29.171:8080/xmlconv/RpcRouter"
server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

method_result = ""
try:
    # open the file XML file and read it into byte array
    infilename = "/home/dev-gso/eea/Reportnet/Dataflows/MMR-PAMs/test/mmr_pams_v20150729.xml"
    fin = open(infilename, 'rb')
    contents = fin.read()
    fin.close()

    # file parameter value encoded as Base64 byte array
    param1 = xmlrpc.client.Binary(contents)
    # convert_id parameter
    param2 = "437"
    # filename paramter
    param3 = "ResultFile.html"
    method_result = server.ConversionService.convertPush(param1, param2, param3)

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
