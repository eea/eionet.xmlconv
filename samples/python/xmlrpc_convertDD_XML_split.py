#   Script for testing XMLCONV Conversion Service
#   The script is converting Excel file to XML.
#
#   Author: VLF


import xmlrpc.client

# server_url="http://converters.eionet.eu.int/RpcRouter"
server = xmlrpc.client.ServerProxy("http://localhost:8080/RpcRouter")

try:
    # excel file
    param1 = "http://cdr.eionet.europa.eu/se/eea/ewn1/envr8broa/SE_Rivers_Revised_SoE2008.xls"
    # sheet name
    param2 = "Stations2"

    method_result = server.ConversionService.convertDD_XML_split(param1, param2)

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

print(method_result)
