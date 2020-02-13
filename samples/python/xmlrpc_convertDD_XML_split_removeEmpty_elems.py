#   Script for testing XMLCONV Conversion Service
#   The script is converting Excel file to XML.
#
#   Author: VLF, Vskiadas


import xmlrpc.client

import sys

server = xmlrpc.client.ServerProxy( sys.argv[1])

try:
    # excel file
    print(sys.argv)
    param1 = sys.argv[2]

    # sheet name
    param2 = sys.argv[3]

    method_result = server.ConversionService.convertDD_XML_split_removeEmptyElems(param1, param2)

except xmlrpc.client.ProtocolError as err:
    print("A protocol error occurred")
    print("URL: %s" % err.url)
    print("HTTP/HTTPS headers: %s" % err.headers)
    print("Error code: %d" % err.errcode)
    print("Error message: %s" % err.errmsg)

convertedFiles = method_result['convertedFiles']
print(method_result['convertedFiles'][0]['content'])
