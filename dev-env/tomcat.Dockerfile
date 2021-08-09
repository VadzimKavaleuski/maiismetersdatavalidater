FROM tomcat:8.5.65-jdk11-adoptopenjdk-openj9
COPY aiis-datavalidater usr/local/tomcat/conf/   
COPY server.xml usr/local/tomcat/conf/  
COPY *.war usr/local/tomcat/webapps/  