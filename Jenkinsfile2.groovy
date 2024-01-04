pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'pwd'
                sh 'mvn clean install'
            }
        }


    }
}
