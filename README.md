## QA and converters (XMLCONV) software

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

### Development Phase, building the .war

The default profile is using the docker maven plugin to setup a mysql database for use with the integration tests phase.
Ideally, the mysql database should be using tmpfs filesystem, in order for the integration tests to run faster.
To create a .war file for deployment with tomcat, you can run

    $ mvn clean install

If you need to use a custom mysql database defined in the local.properties file, you can create a .war file using:

    $ mvn -Plocal clean install

To skip the integration tests (not recommended), you can add -Dmaven.test.skip=true e.g

    $ mvn -Plocal clean install -Dmaven.test.skip=true

### Runtime configuration

Now you need to register to the JVM the key/value pairs inside the local.properties file. In order to do that you have 2 options:

1. Add all properties to JVM using the CATALINA_OPTS variable that tomcat reads before it deploys the war file:
	
	
		$ vim ${CATALINA_HOME}/bin/setenv.sh

Manually add all key/value pairs that are inside the local.properties file.

	CATALINA_OPTS="-Dapp.home=/home/user/xmlconv -D...."

2. By convention the xmlconv application will search for a system property with the name docker.config.xmlconv. This system property should contain the absolute path for local.properties file. When the application starts if the path exists it will load and cache all the key-values defined in the local.properties. In order to register the values to the JVM you need to:

		$ vim ${CATALINA_HOME}/bin/setenv.sh

Inside setenv.sh

	CATALINA_OPTS="-Ddocker.config.xmlconv=/home/user/local.properties -Dapp.host=localhost:8080 -Dconfig.log.file=/tmp/xmlconv.log"
