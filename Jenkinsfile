node {
  stage('Preparation') { 
    deleteDir()
    git credentialsId: 'a29845c7-b29e-44ce-ab58-515727dba9ef',
        url:'https://VadimKA1975@bitbucket.org/VadimKA1975/maiisdatavalidater.git',
  }
  stage('Build') {
    sh 'mvn  clean install'
  }
  stage('prepare deploy') {
    dir('deploy') {
      sh 'ls'
    }  
    withCredentials([string(credentialsId: 'aiis-datavalidater-login', variable: 'aiis_datavalidater_login'),string(credentialsId: 'aiis-datavalidater-password', variable: 'aiis_datavalidater_password')]){
      String confFile = readFile('dev-env/aiis-datavalidater')
        .replaceAll('aiis-datavalidater-login',aiis_datavalidater_login)
        .replaceAll('aiis-datavalidater-password',aiis_datavalidater_password)
      writeFile file:'deploy/aiis-datavalidater', text: confFile
    }  
     sh 'cp dev-env/server.xml deploy/'
     sh 'cp dev-env/tomcat.Dockerfile deploy/'
    sh 'ls '
    sh 'cp target/AIISDateValidater.war deploy/AIISDateValidater.war'
    dir('deploy') {
      stash 'copy2docker'
    }  
  }  
}  
node ('docker'){   
  stage('deploy') {
    deleteDir()
    unstash 'copy2docker'
    sh 'docker ps'
    sh 'docker ps|grep tcDV4dev >/dev/null 2>/dev/null||echo "noting"'
    sh 'docker ps|grep tcDV4dev >/dev/null 2>/dev/null&&docker stop tcDV4dev||echo "noting"'
    sh 'docker ps -a|grep tcDV4dev >/dev/null 2>/dev/null&&docker rm tcDV4dev||echo "noting"'
    sh 'docker images|grep tcDV4devimage >/dev/null 2>/dev/null&&docker rmi tcDV4devimage||echo "noting"'
    sh 'docker build -f tomcat.Dockerfile . -t tcDV4devimage'
    sh 'docker run --name tcDV4dev --restart=on-failure -p 192.168.0.1:8100:8009 -d tcDV4devimage  '
  }  
}
