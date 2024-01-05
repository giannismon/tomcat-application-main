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
                sh 'll'
                sh "echo '##########################################################'"

                stash name: 'buildResults', includes: '/target/helloworld.war' 
            }
        }


    }
}
