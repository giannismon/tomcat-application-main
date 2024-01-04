pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'cd git'
                sh 'mvn clean install'
            }
        }


    }
}
