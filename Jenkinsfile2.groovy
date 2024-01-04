pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'sudo chown 777 *'
                sh 'sudo mvn clean install'
            }
        }


    }
}
