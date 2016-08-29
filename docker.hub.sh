#!/bin/bash
APP=eeacms/xmlconv
BUILDTIME=$(date '+%Y-%m-%dT%H%M')

# docker build runs only if tests are successful
mvn clean install && docker build -t $APP:latest . &&  docker tag $APP:latest $APP:$BUILDTIME && docker push $APP:latest && docker push $APP:$BUILDTIME
