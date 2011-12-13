call "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\bin\shutdown.bat"
call mvn clean install -Dmaven.test.skip=true
pause
rd /S /Q "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\webapps\xmlconv"
rd /S /Q "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\work\Catalina\localhost\xmlconv"
del /S /Q "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\conf\Catalina\localhost\xmlconv.xml"
copy /Y .\target\xmlconv.war "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\webapps\xmlconv.war"
call "C:\Program Files\Apache Software Foundation\apache-tomcat-6.0.32\bin\startup.bat"
