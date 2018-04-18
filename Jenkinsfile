pipeline {
  agent { node { label 'dockera5db6c37' } }
  tools {
    maven 'maven3'
    jdk 'Java8'
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '4', artifactNumToKeepStr: '2'))
  }
  stages {
    stage('Project Build') {
      steps {
        sh 'mvn clean -B -V verify pmd:pmd pmd:cpd findbugs:findbugs checkstyle:checkstyle'
      }
    }
    stage('Results') {
      steps {
        junit '**/target/failsafe-reports/*.xml'
        pmd canComputeNew: false
        dry canComputeNew: false
        checkstyle canComputeNew: false
        findbugs pattern: '**/findbugsXml.xml'
        openTasks canComputeNew: false
        // TODO: add cobertura or jacoco build configuration
        //cobertura failNoReports: true
        archive 'target/*.war'
      }
    }
    stage('Docker push') {
      steps {
          timeout(time: 60, unit: 'MINUTES') {
            script {
              def date = sh(returnStdout: true, script: 'echo $(date "+%Y-%m-%dT%H%M")').trim()
              image = docker.build("sofiageo/xmlconv:latest")
              docker.withRegistry('https://index.docker.io/v1/', 'sofiageo-hub') {
                image.push()
                image.push(date)
              }
            }
          }
        }
    }
  }
}
