FROM mytomcat:latest
COPY aiis-datavalidater /opt/tomcat/conf/   
COPY server.xml /opt/tomcat/conf/  
COPY *.war /opt/tomcat/webapps/  