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
                sh "echo '##########################################################'"
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
                sh "echo '##########################################################'"
                sh 'hostname'
                sh 'pwd'
                cleanWs()
                sh 'ls'
                unstash 'buildResults'
                sh 'ls target'
                sh 'rm -rf /root/apache-tomcat-9.0.70/webapps/helloworld*'
                sh 'mv **/*.war /root/apache-tomcat-9.0.70/webapps/'
                sh 'ls /root/apache-tomcat-9.0.70/webapps/'
                sh "echo '##########################################################'"

            }
        }


        stage('Restart tomcat') {
            agent {
                label 'dev'
            }
            steps {
                sh "echo '##########################################################'"
                sh 'systemctl stop tomcat'
                sh 'sleep 5' // Περιμένει 5 δευτερόλεπτα
                sh 'systemctl start tomcat'
                sh "echo '##########################################################'"

            }
        }


        stage('Check Tomcat Status') {
            agent {
                label 'dev'
            }
            steps {
                script {
                    sh "echo '##########################################################'"


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
                    sh "echo '##########################################################'"

                    } else {
                        error 'Tomcat process not found.'
                    }
                }
            }
        }




        stage('Ping URL') {
            agent {
                label 'dev'
            }
            steps {
                script {
                    def response = sh(script: "wget --spider -S http://192.168.1.8:8080/helloworld/ 2>&1 | grep 'HTTP/1.1 200'", returnStatus: true)

                    if (response == 0) {
                        echo "Ping successful."
                    } else {
                        error "Failed to ping."
                    }
                }
            }
        }

    }

}