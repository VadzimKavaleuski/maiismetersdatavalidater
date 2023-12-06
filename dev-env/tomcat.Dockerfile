FROM tomcat:jre21
COPY aiis-datavalidater /usr/local/tomcat/conf/   
#COPY server.xml /usr/local/tomcat/conf/  
COPY AIISDataValidater.war /usr/local/tomcat/webapps/ROOT.war 