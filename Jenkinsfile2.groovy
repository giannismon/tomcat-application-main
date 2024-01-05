pipeline {
    agent any

    tools {
        maven "Maven"
    }

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'mvn clean install'
                sh 'pwd'
                stash name: 'buildResults', includes: '/git/target/helloworld.war' 
            }
        }

        stage('Transfer to Node with Tomcat 9') {
            agent {
                label 'dev'
            }
            steps {
                sh 'hostname'
                sh 'pwd'
                unstash 'buildResults'
            }

        }
    }
}
