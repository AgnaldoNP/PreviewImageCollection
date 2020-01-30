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
                    sh './gradlew tasks' //run a gradle task
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
