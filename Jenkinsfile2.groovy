pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'cd git'
                sh 'pwd'
                sh 'mvn clean install'
            }
        }


    }
}
