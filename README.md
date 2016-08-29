## QA and Converters (XMLCONV) software

### Prerequisites

* Java 1.8
* Maven 3.3.9
* Tomcat 8 or higher
* MySQL 5.5
* Docker 1.6 or higher

## Installation Guide

Create local.properties file by copying the contents from default.properties as an example.
Edit the values of system paths, database url and other properties to what is relevant.

"app.home" property represents the system folder which is the root for subfolders where all content files will be stored.
Tomcat user should have write permissions on "app.home" subdirectories.

### Development Phase - Building the .war

The default profile is using the docker maven plugin to setup a mysql database for use with the integration tests phase.
Ideally, the mysql database should be using tmpfs filesystem, in order for the integration tests to run faster.
To create a .war file for deployment with tomcat, you can run

    $ mvn clean install

If you want to use a custom mysql database for unit tests, define in the local.properties file the config.test.* properties for your own database, and then build the WAR :

    $ mvn -Plocal clean install

To skip the integration tests (not recommended), you can add -Dmaven.test.skip=true e.g

    $ mvn -Plocal clean install -Dmaven.test.skip=true

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

