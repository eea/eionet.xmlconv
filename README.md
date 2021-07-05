## QA and Converters (XMLCONV) software

### Prerequisites

* Java 1.8
* Maven 3.3.9
* Tomcat 8.0 or higher
* MySQL 5.5
* Docker 1.12 or higher

## Installation Guide

Create local.properties file by copying the contents from default.properties as an example.
Edit the values of system paths, database url and other properties to what is relevant.

"app.home" property represents the system folder which is the root for subfolders where all content files will be stored.
Tomcat user should have write permissions on "app.home" subdirectories.
    
### Development Phase - Building the .war

The default profile is using the docker maven plugin to setup a mysql database for use with the integration tests phase.
Ideally, the mysql database should be using tmpfs filesystem, in order for the integration tests to run faster.
To create a .war file for deployment with tomcat, you can run any of the following commands:

To run unit and integration tests before building, run:

    $ mvn clean install -Denv=jenkins

To skip the integration tests (not recommended), you can add -Dmaven.test.skip=true e.g

    $ mvn clean install -Dmaven.test.skip=true

### Runtime configuration

The Xmlconv application needs a number of configuration options, such as database connection information, FME credentials, BaseX server config etc.

Every configuration option can be provided either in the properties file that the app is built with, or with an environmental variable at runtime. 
The environmental configuration of a property is used if found. 
This way, whatever the local.properties the app was built with, it can run at any host through environment configuration, for example by changing the database config and app.home.
A helpful configuration key named initial.admin can be set to bootstrap in the ACLs the initial administrators at runtime. 
In case you want to provide environmentally the key/value pairs you have to set CATALINA_OPTS in Tomcat with the key/value pairs you want to set:

e.g. CATALINA_OPTS=-Dapp.home=/opt/xmlconv -Dapp.host=converstest.eionet.europa.eu -Dconfig.db.jdbcurl=jdbc:mysql://mysql:3306/xmlconv -Dconfig.db.driver=com.mysql.jdbc.Driver -Dconfig.db.user=root -Dconfig.db.password=xxxxx -Dbasexserver.host=basex -Dbasexserver.port=1984 -Dbasexserver.user=admin -Dbasexserver.password=admin

### Docker configuration

After having built the WAR file with maven, it can be directly used in docker containers thanks to the environmental configuration. The Dockerfile can be used to build a ready-to-deploy image of xmlconv :

    $ docker build -t eeacms/xmlconv:latest .

There is a script docker.hub.sh that builds the WAR and pushes to the Docker Hub, one to the latest tag, and one with a timestamp, for versioning of images.

### Rancher deployments

An example docker-compose for usage on Rancher deployments can be found on docker/xmlconv along with an example environment file.

**Important Note:**
When deploying to rancher platform, any environment variables provide to tomcat container through the *CATALINA_OPTS* variable, should not include new lines for
tomcat versions 9.0.24 and above. 

### Parameters configuration for mock application
For test purposes a mechanism has been added to mimic long running jobs. This mechanism uses maven class shadowing to override 2 key classes of 
basex library (the library used to run xquery scripts), providing timeouts and frequent control checks in order to halt a long running job. When 
below parameters are set in a testing environment all CR calls will be directed to a mock cr application which has been created for this purpose 
and what really does is returning a file (like the sparql results that CR would sent) but very slowly, in order to mimic a very busy CR with a 
very bad Sparql query. In a production environment redirection to mock application should be disabled and calls to CR will proceed normally.

#### parameters config for test environment
* config.cr.host=https://cr.eionet.europa.eu
* config.cr.mockCrUrl=http://mockxquerydelay.ewxdevel1dub.eionet.europa.eu
* config.enableXqueryCrCallsInterception=true

#### parameters config for production
* config.cr.host=                             
* config.cr.mockCrUrl=                        
* config.enableXqueryCrCallsInterception=false             

#### Setup a dockerized rabbitmq instance locally:
docker run -d --hostname my-rabbit -p 0.0.0.0:15672:15672 -p 5672:5672 --name some-rabbit -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password rabbitmq:3-management