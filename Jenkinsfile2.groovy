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
                sh "echo '##########################################################'"

                stash includes : "*", name : "buildResults"
            }
        }


    }
}
