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

    stage ('Unit Tests and Sonarqube') {
      when {
        not { buildingTag() }
      }
      steps {
                withSonarQubeEnv('Sonarqube') {
                    sh '''mvn clean -B -V -P docker verify  '''
             /**       sh '''try=2; while [ \$try -gt 0 ]; do curl -s -XPOST -u "${SONAR_AUTH_TOKEN}:" "${SONAR_HOST_URL}api/project_tags/set?project=${GIT_NAME}-${BRANCH_NAME}&tags=${SONARQUBE_TAGS},${BRANCH_NAME}" > set_tags_result; if [ \$(grep -ic error set_tags_result ) -eq 0 ]; then try=0; else cat set_tags_result; echo "... Will retry"; sleep 60; try=\$(( \$try - 1 )); fi; done'''
               **/
                }
      }
      post {
        always {
            junit 'target/failsafe-reports/*.xml'
            jacoco(
                execPattern: 'target/*.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java',
                exclusionPattern: 'src/test*'
            )
            publishHTML target:[
               allowMissing: false,
               alwaysLinkToLastBuild: false,
               keepAll: true,
               reportDir: 'target/site/jacoco',
               reportFiles: 'index.html',
               reportName: "Detailed Coverage Report"
            ]
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

post {
    always {
      cleanWs(cleanWhenAborted: true, cleanWhenFailure: true, cleanWhenNotBuilt: true, cleanWhenSuccess: true, cleanWhenUnstable: true, deleteDirs: true)

      script {

        def url = "${env.BUILD_URL}/display/redirect"
        def status = currentBuild.currentResult
        def subject = "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        def summary = "${subject} (${url})"
        def details = """<h1>${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${status}</h1>
                         <p>Check console output at <a href="${url}">${env.JOB_BASE_NAME} - #${env.BUILD_NUMBER}</a></p>
                      """

        def color = '#FFFF00'
        if (status == 'SUCCESS') {
          color = '#00FF00'
        } else if (status == 'FAILURE') {
          color = '#FF0000'
        }

        echo "Recipients are mf@eworx.gr,nta@eworx.gr,vs@eworx.gr,sp@eworx.gr"

        emailext(
        to: 'mf@eworx.gr;nta@eworx.gr;vs@eworx.gr;sp@eworx.gr',
        subject: '$DEFAULT_SUBJECT',
        body: details,
        attachLog: true,
        compressLog: true,
        )

      }
    }
  }
  
}
