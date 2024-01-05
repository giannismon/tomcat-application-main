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
                stash name: 'buildResults', includes: '/var/lib/jenkins/workspace/git/target/helloworld.war' 
            }
        }

        stage('Transfer to Node with Tomcat 9') {
            agent {
                label 'dev'
            }
            steps {
                script {
                    // Εκτελεί την εντολή SCP για τη μεταφορά του αρχείου .war
                    unstash 'buildResults'
                }
            }
        }
    }
}
