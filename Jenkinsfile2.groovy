pipeline {
    agent none

    tools {
        maven "Maven"
    }


    environment {
        // This can be nexus3 or nexus2
        NEXUS_VERSION = "nexus3"
        // This can be http or https
        NEXUS_PROTOCOL = "http"
        // Where your Nexus is running
        NEXUS_URL = "192.168.1.99:8081"
        // Repository where we will upload the artifact
        NEXUS_REPOSITORY = "http://192.168.1.99:8081/repository/LoginWebApp/"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus"
        ARTIFACT_VERSION = "${BUILD_NUMBER}"
    }





    stages {

        stage('Verify Environment') {
            steps {
                script {
                    echo "NEXUS_VERSION: ${NEXUS_VERSION}"
                    echo "NEXUS_PROTOCOL: ${NEXUS_PROTOCOL}"
                    echo "NEXUS_URL: ${NEXUS_URL}"
                    echo "NEXUS_REPOSITORY: ${NEXUS_REPOSITORY}"
                    echo "NEXUS_CREDENTIAL_ID: ${NEXUS_CREDENTIAL_ID}"
                    echo "ARTIFACT_VERSION: ${ARTIFACT_VERSION}"
                }
            }
        }



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
                sh 'mvn clean install'
                sh 'hostname'
                sh 'pwd'
                sh 'ls'
                sh 'ls target'
                sh "echo '##########################################################'"

                stash includes: "target/**", name: "buildResults"
            }
        }



        stage("publish to nexus") {
            agent {
                label 'master'
            }
            steps {
                script {
                    sh "echo 'UPLOADDDDDDDDDDDDDDDDDDDDDDDDDD ##########################################################'"
                    // Read POM xml file using 'readMavenPom' step , this step 'readMavenPom' is included in: https://plugins.jenkins.io/pipeline-utility-steps
                    pom = readMavenPom file: "pom.xml";
                    // Find built artifact under target folder
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    // Print some info from the artifact found
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    // Extract the path from the File found
                    artifactPath = filesByGlob[0].path;
                    // Assign to a boolean response verifying If the artifact name exists
                    artifactExists = fileExists artifactPath;

                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";

                        nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: ARTIFACT_VERSION,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [
                                // Artifact generated such as .jar, .ear and .war files.
                                [artifactId: pom.artifactId,
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging]
                            ]
                        );

                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
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




    }

}