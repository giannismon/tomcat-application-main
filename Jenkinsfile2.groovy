pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'chown 777 *'
                sh 'mvn clean install'
            }
        }


    }
}
