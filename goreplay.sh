#!/bin/bash
 cd /gor_dir
 chmod +x gor
 ./gor --input-raw :8080 --output-http=http://converters.devel6cph.eea.europa.eu --http-set-header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZW1vX3VzZXIiLCJhdWQiOiJlZWEiLCJpc3MiOiJlZWEifQ.ZCtBmOKkiWnukXaKuw2wk3DSDgL9cHCUYJi2wXyEtQo"