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
                sh 'mv target/*.war /root/apache-tomcat-9.0.70/webapps/'
                sh 'ls /root/apache-tomcat-9.0.70/webapps/'

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
