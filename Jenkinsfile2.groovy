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
                scp 'target/helloworld.war root@192.168.1.8:/root/apache-tomcat-9.0.70/webapps/'
            }
        }


    }
}
