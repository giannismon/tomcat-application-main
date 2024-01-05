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
                sh 'hostname'
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
                sh 'hostname'
                sh 'pwd'
                sh 'ls'
                stash includes : "*", name : "buildResults"
                sh 'mv **/*.war /root/apache-tomcat-9.0.70/webapps/'
                sh 'ls /root/apache-tomcat-9.0.70/webapps/'
            }
        }







    }
}
