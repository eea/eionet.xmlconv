
## QA and Converters (XMLCONV) Software    

### Prerequisites:

* Java 1.6
* Maven 2.0.4 or higher
* Tomcat 5.5 or higher
* MySql 5.5 or higher


## Quick Installation Guide:

Create local.properties file by copying the contents from default.properties as an example.
Edit the values of system paths, DB url and other proeprties to what is relevant.

"app.home" property represents the system folder which is the root for subfolders where all content files will be stored.
Tomcat user should have write permissions on "app.home" subfolders.

If it is a first time install then acl folder should be copied manually into ${app.home}/acl



### Building the war:

For developers that want to override properties at compile time, they can use the development profile

	$ mvn -P development clean install -Dmaven.test.skip=true

If you want to resolve the placeholders inside the properties file when the application is deployed then:
	
	$ mvn -P docker clean install -Dmaven.test.skip=true


Now you need to register to the JVM the key/value pairs inside the local.properties file. In order to do that you have 2 options:

1. Add all properties to JVM using the CATALINA_OPTS variable that tomcat reads before it deploys the war file:
	
	
		$ vim ${CATALINA_HOME}/bin/setenv.sh

Manually add all key/value pairs that are inside the local.properties file.

	CATALINA_OPTS="-Dapp.home=/home/user/xmlconv -D...."

2. By convention the xmlconv application will search for a system property with the name docker.config.xmlconv. This system property should contain the absolute path for local.properties file. When the application starts if the path exists it will load and cache all the key-values defined in the local.properties. In order to register the values to the JVM you need to:

		$ vim ${CATALINA_HOME}/bin/setenv.sh

Inside setenv.sh

	CATALINA_OPTS="-Ddocker.config.xmlconv=/home/user/local.properties -Dapp.host=localhost:8080 -Dconfig.log.file=/tmp/xmlconv.log"
