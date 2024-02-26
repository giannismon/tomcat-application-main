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
        NEXUS_REPOSITORY = "LoginWebApp"
        // Jenkins credential id to authenticate to Nexus OSS
        NEXUS_CREDENTIAL_ID = "nexus"
        ARTIFACT_VERSION = "${BUILD_NUMBER}"
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




        stage("Publish to Nexus Repository Manager") {
            agent {
                label 'master'
            }
            steps {
                script {
                    pom = readMavenPom file: "pom.xml";
                    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
                    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
                    artifactPath = filesByGlob[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
                        nexusArtifactUploader(
                            nexusVersion: 'nexus3',
                            protocol: 'http',
                            nexusUrl: '192.168.1.99:8081',
                            groupId: 'org.junit.jupiter',
                            version: '1.0-SNAPSHOT',
                            repository: 'LoginWebApp',
                            credentialsId: 'nexus',
                            artifacts: [
                                [artifactId: 'helloworld',
                                classifier: '',
                                file: artifactPath,
                                type: pom.packaging],
                                [artifactId: 'helloworld',
                                classifier: '',
                                file: "pom.xml",
                                type: "pom"]
                            ]
                        );
                    } else {
                        error "*** File: ${artifactPath}, could not be found";
                    }
                }
            }
        }



    }

}