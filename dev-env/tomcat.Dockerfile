FROM mytomcat:latest
COPY chmod 666 aiis-datavalidater /opt/tomcat/conf/   
COPY server.xml /opt/tomcat/conf/  
COPY *.war /opt/tomcat/webapps/  