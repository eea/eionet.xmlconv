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


/**
stage ('Unit Tests and Sonarqube') {
      when {
        not { buildingTag() }
      }
      steps {
                withSonarQubeEnv('Sonarqube') {
                    sh '''mvn clean -B -V -P docker verify cobertura:cobertura-integration-test pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle surefire-report:report sonar:sonar -Dsonar.sources=src/main/java/ -Dsonar.junit.reportPaths=target/failsafe-reports -Dsonar.cobertura.reportPath=target/site/cobertura/coverage.xml -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} -Dsonar.java.binaries=target/classes -Dsonar.java.test.binaries=target/test-classes -Dsonar.projectKey=${GIT_NAME}-${GIT_BRANCH} -Dsonar.projectName=${GIT_NAME}-${GIT_BRANCH}'''
                    sh '''try=2; while [ \$try -gt 0 ]; do curl -s -XPOST -u "${SONAR_AUTH_TOKEN}:" "${SONAR_HOST_URL}api/project_tags/set?project=${GIT_NAME}-${BRANCH_NAME}&tags=${SONARQUBE_TAGS},${BRANCH_NAME}" > set_tags_result; if [ \$(grep -ic error set_tags_result ) -eq 0 ]; then try=0; else cat set_tags_result; echo "... Will retry"; sleep 60; try=\$(( \$try - 1 )); fi; done'''
                }
      }
      post {
        always {
            junit 'target/failsafe-reports/*.xml'
            cobertura coberturaReportFile: 'target/site/cobertura/coverage.xml'
        }
      }
    }

    **/

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
