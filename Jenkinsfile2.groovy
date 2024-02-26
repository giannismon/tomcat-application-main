pipeline {
    agent none

    tools {
        maven "Maven"
    }



    stages {



        stage("Check out") {
            agent {
                label 'master'
            }
            steps {
                script {
                    git branch: 'master', url: 'https://github.com/giannismon/tomcat-application-main.git';
                }
            }
        }



        stage('Build') {
            agent {
                label 'master'
            }
            steps {
                sh "echo '##########################################################'"
                sh 'mvn clean package'

            }
        }


    }

}