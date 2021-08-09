FROM tomcat:8.5.65-jdk11-adoptopenjdk-openj9
COPY aiis-datavalidater /opt/tomcat/conf/   
COPY server.xml /opt/tomcat/conf/  
COPY *.war /opt/tomcat/webapps/  