pipeline {

    agent {
        label "master"
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Unit & Integration Tests') {
            steps {
                script {
                    try {
                        sh './gradlew clean assembleDebug --no-daemon' //run a gradle task
                    } finally {
                        junit '**/build/test-results/test/*.xml'
                        //make the junit test results available in any case (success & failure)
                    }
                }
            }
        }

        stage('Publish Artifact to Nexus') {
            steps {
                sh './gradlew publish --no-daemon'
            }
        }
    }
    post {
        always { //Send an email to the person that broke the build
            step([$class                  : 'Mailer',
                  notifyEveryUnstableBuild: true,
                  recipients              : [emailextrecipients([[$class: 'CulpritsRecipientProvider'], [$class: 'RequesterRecipientProvider']])].join(' ')])
        }
    }

}
