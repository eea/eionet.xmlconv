pipeline {
  agent {
            node { label "docker-host" }
  }
  
  environment {
    GIT_NAME = "eionet.xmlconv"
    SONARQUBE_TAGS = "converters.eionet.europa.eu"
    registry = "eeacms/xmlconv"
    availableport = sh(script: 'echo $(python3 -c \'import socket; s=socket.socket(); s.bind(("", 0)); print(s.getsockname()[1], end = ""); s.close()\');', returnStdout: true).trim();
    availableport2 = sh(script: 'echo $(python3 -c \'import socket; s=socket.socket(); s.bind(("", 0)); print(s.getsockname()[1], end = ""); s.close()\');', returnStdout: true).trim();
    availableport3 = sh(script: 'echo $(python3 -c \'import socket; s=socket.socket(); s.bind(("", 0)); print(s.getsockname()[1], end = ""); s.close()\');', returnStdout: true).trim();

  }


  tools {
    maven 'maven3'
    jdk 'Java11'
  }

  stages {
    stage('Project Build') {
      steps {
          sh 'mvn clean -B -V verify  -Dmaven.test.skip=true'
      }
      post {
          success {
          archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                    }
      }
    }

        stage ('Docker build and push') {
      when {
          environment name: 'CHANGE_ID', value: ''
      }
      steps {
        script{

                 if (env.BRANCH_NAME == 'master') {
                         tagName = 'latest'
                 } else {
                         tagName = "$BRANCH_NAME"
                 }
                 def date = sh(returnStdout: true, script: 'echo $(date "+%Y-%m-%dT%H%M")').trim()
                 dockerImage = docker.build("$registry:$tagName", "--no-cache .")
                 docker.withRegistry( '', 'eeajenkins' ) {
                          dockerImage.push()
                           dockerImage.push(date)
                 }
            }
      }
      post {
        always {
                           sh "docker rmi $registry:$tagName | docker images $registry:$tagName"
        }

        }
    }



  }

  
}
