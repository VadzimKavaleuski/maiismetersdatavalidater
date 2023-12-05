// node {
//   stage('Preparation') { 
//     deleteDir()
//     git credentialsId: 'github.VadzimKavaleuski',
//         url:'git@github.com:VadzimKavaleuski/maiismetersdatavalidater.git'
//   }
// }
podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9.5-sapmachine-11
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
"""
  ) {
    node(POD_LABEL) {
      stage('Preparation') { 
        git credentialsId: 'github.VadzimKavaleuski',
            url:'git@github.com:VadzimKavaleuski/maiismetersdatavalidater.git'
      }
      stage('Dependency preparation') { 
        sh 'mkdir aiisutils'
        dir('aiisutils') {
          git credentialsId: 'github.VadzimKavaleuski',
              url:'git@github.com:VadzimKavaleuski/mAIISUtils.git'
        }
      }
      stage('Dependency Build') {
        container('maven') {
          dir('aiisutils') {
            sh 'ls -la'
            sh 'mvn clean install'
          }
        }
      }





      stage('Build') {
        container('maven') {
          sh 'ls -la'
          sh 'ls -la /usr/bin'
          sh 'mvn clean install'
        }
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
        sh 'cp target/AIISDataValidater.war deploy/AIISDataValidater.war'
        dir('deploy') {
          stash 'copy2docker'
        }  
      }
    }
  }



  // stage('Build') {
  //   sh 'mvn  clean install'
  // }
  // stage('prepare deploy') {
  //   dir('deploy') {
  //     sh 'ls'
  //   }  
  //   withCredentials([string(credentialsId: 'aiis-datavalidater-login', variable: 'aiis_datavalidater_login'),string(credentialsId: 'aiis-datavalidater-password', variable: 'aiis_datavalidater_password')]){
  //     String confFile = readFile('dev-env/aiis-datavalidater')
  //       .replaceAll('aiis-datavalidater-login',aiis_datavalidater_login)
  //       .replaceAll('aiis-datavalidater-password',aiis_datavalidater_password)
  //     writeFile file:'deploy/aiis-datavalidater', text: confFile
  //   }  
  //    sh 'cp dev-env/server.xml deploy/'
  //    sh 'cp dev-env/tomcat.Dockerfile deploy/'
  //   sh 'ls '
  //   sh 'cp target/AIISDataValidater.war deploy/AIISDataValidater.war'
  //   dir('deploy') {
  //     stash 'copy2docker'
  //   }  
  // }  
// }  
// node ('docker'){   
//   stage('deploy') {
//     deleteDir()
//     unstash 'copy2docker'
//     sh 'docker ps'
//     sh 'docker ps|grep tcdv4dev >/dev/null 2>/dev/null||echo "noting"'
//     sh 'docker ps|grep tcdv4dev >/dev/null 2>/dev/null&&docker stop tcdv4dev||echo "noting"'
//     sh 'docker ps -a|grep tcdv4dev >/dev/null 2>/dev/null&&docker rm tcdv4dev||echo "noting"'
//     sh 'docker images|grep tcdv4devimage >/dev/null 2>/dev/null&&docker rmi tcdv4devimage||echo "noting"'
//     sh 'docker build -f tomcat.Dockerfile . -t tcdv4devimage'
//     sh 'docker run --name tcdv4dev --restart=on-failure -p 192.168.0.1:8110:8009 -d tcdv4devimage  '
//   }  
// }
  podTemplate(yaml: """
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
    envVar:
    - key: tagName
      value: ${env.BRANCH_NAME}
"""
  ) {

    node(POD_LABEL) {
        stage('build docker image') {
            unstash 'copy2docker'
            container('kaniko') {
                stage('Build a Maven project') {
                    sh 'ls -la'
                    echo '${tagName}'
                    echo '$tagName'
                    sh 'echo "$tagName - $tagName"'
                    sh '/kaniko/executor --context . --dockerfile tomcat.Dockerfile --destination kube-registry-nodeport.registry.svc.cluster.local:5000/aiismetersdatavalidator:master'
                    // sh '/kaniko/executor --context . --dockerfile tomcat.Dockerfile --destination kube-registry-nodeport.registry.svc.cluster.local:5000/aiismetersdatavalidator:${tagName}'
                }
            }
        }
    }
 }

  

