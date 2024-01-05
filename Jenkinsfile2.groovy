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
                //sh 'mvn clean install'
                sh 'pwd'
                sh 'ls'
                sh "echo '##########################################################'"

                stash includes : "*", name : "buildResults"
            }
        }



        stage('tomcat') {
            agent {
                label 'dev'
            }
            steps {
                sh 'pwd'
                sh 'ls'
                stash includes : "*", name : "buildResults"
            }
        }







    }
}
