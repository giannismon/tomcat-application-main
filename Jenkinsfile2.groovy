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
                sh 'hostname'
                sh 'pwd'
                sh 'ls'
                sh 'ls target'
                sh "echo '##########################################################'"

                stash includes: "target/**", name: "buildResults"
            }
        }



        stage('Deploy tomcat') {
            agent {
                label 'dev'
            }
            steps {
                sh 'hostname'
                sh 'pwd'
                cleanWs()
                sh 'ls'
                unstash 'buildResults'
                sh 'ls target'
                sh 'mv **/*.war /root/apache-tomcat-9.0.70/webapps/'
                sh 'ls /root/apache-tomcat-9.0.70/webapps/'
            }
        }


        stage('Restart tomcat') {
            agent {
                label 'dev'
            }
            steps {
                sh '/root/apache-tomcat-9.0.70/bin/shutdown.sh'
                sh 'sleep 5' // Περιμένει 5 δευτερόλεπτα
                sh '/root/apache-tomcat-9.0.70/bin/startup.sh'
            }
        }


        stage('Check Tomcat Status') {
            agent {
                label 'dev'
            }
            steps {
                script {
                    def tomcatProcess = sh(script: 'ps aux | grep "[t]omcat"', returnStatus: true)

                    if (tomcatProcess == 0) {
                        echo 'Tomcat process found. Checking if it is actually Tomcat...'

                        // Check if the process is really Tomcat
                        def isTomcat = sh(script: 'ps aux | grep "[t]omcat" | grep "catalina.base"', returnStatus: true)

                        if (isTomcat == 0) {
                            echo 'Tomcat is running.'
                        } else {
                            error 'Process found, but it may not be Tomcat.'
                        }
                    } else {
                        error 'Tomcat process not found.'
                    }
            }
        }



    }
}
