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
                    sh './gradlew clean app:assembleDebug --no-daemon' //run a gradle task
                }
            }
        }

        stage('Publish Artifact to Nexus') {
            steps {
                sh './gradlew publish --no-daemon'
            }
        }
    }
}
