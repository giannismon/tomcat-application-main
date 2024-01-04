pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                script {
                    def tomcatHome = '/root/tomcat'  // Αντικατέστησε με το πραγματικό path του Tomcat

                    sh "cp /var/lib/jenkins/workspace/tomcat/target/*.war $tomcatHome/webapps/"
                    sh "$tomcatHome/bin/shutdown.sh"
                    sh "$tomcatHome/bin/startup.sh"
                }
            }
        }

        // Άλλα στάδια προστίθενται εδώ
    }
}
