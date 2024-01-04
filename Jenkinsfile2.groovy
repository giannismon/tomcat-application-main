pipeline {
    agent none

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
            }
        }


    }
}
