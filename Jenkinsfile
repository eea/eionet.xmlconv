pipeline {
  agent { node { label 'docker-1.13' } }
  tools {
    maven 'maven3'
    jdk 'Java11'
  }
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '4', artifactNumToKeepStr: '2'))
    timeout(time: 60, unit: 'MINUTES')
  }
  stages {
    stage('Project Build') {
      steps {
          sh 'mvn clean -B -V verify  -Dmaven.test.skip=true'
      }
      post {
          success {
              archive 'target/*.war'
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



    stage('Static analysis') {
      steps {
        sh 'mvn clean -B -V -Pcobertura verify cobertura:cobertura pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle'
      }
      post {
        always {
            junit '**/target/failsafe-reports/*.xml'
            pmd canComputeNew: false
            dry canComputeNew: false
            checkstyle canComputeNew: false
            findbugs pattern: '**/target/findbugsXml.xml'
            openTasks canComputeNew: false
            cobertura coberturaReportFile: '**/target/site/cobertura/coverage.xml', failNoReports: true
        }
      }
    }
  }
}
