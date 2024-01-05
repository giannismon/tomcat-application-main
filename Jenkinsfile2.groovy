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
                stash name: 'buildResults', includes: '/target/helloworld.war' 
                sh "echo '##########################################################'"
            }
        }


    }
}
