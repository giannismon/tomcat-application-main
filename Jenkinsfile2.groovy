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

        stage('Transfer to Node with Tomcat 9') {
            agent {
                label 'master'
            }
            steps {
                script {
                    // Εκτελεί την εντολή SCP για τη μεταφορά του αρχείου .war
                    sh 'scp /var/lib/jenkins/workspace/git/target/helloworld.war root@192.168.1.10:/root/tomcat/webapps/'
                }
            }
        }
    }
}
