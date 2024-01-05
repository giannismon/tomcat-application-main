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
                sh 'll /var/lib/jenkins/workspace/git'
                sh "echo '##########################################################'"

                stash "*", "buildResults"
            }
        }


    }
}
