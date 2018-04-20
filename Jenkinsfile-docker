pipeline {
  agent { node { label 'docker-1.13' } }
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
            sh 'mvn clean -B -V verify'
        }
        post {
            success {
                archive 'target/*.war'
            }
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
