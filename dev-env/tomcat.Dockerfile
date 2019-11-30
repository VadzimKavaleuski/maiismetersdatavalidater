FROM mytomcat:latest
COPY aiisuserkabinet /opt/tomcat/conf/   
COPY server.xml /opt/tomcat/conf/  
COPY *.war /opt/tomcat/webapps/  