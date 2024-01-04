pipeline {
    agent none

    stages {
        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Deploy to Tomcat') {
            agent {
                label 'dev'
            }
            steps {
                script {
                    // Κάνε την απαραίτητη ενέργεια για να αντιγράψεις το αρχείο στον Tomcat
                    sh "cp target/*.war /path/to/tomcat/webapps/"
                }
            }
        }
    }
}
