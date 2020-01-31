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

        stage('Build app') {
            steps {
                script {
                    sh 'export ANDROID_SERIAL=0047758577 ; ./gradlew clean assembleDebug ; ls'

                }
            }
        }

        stage('Archive apk') {
            steps {
                script {
                    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/debug/app-debug.apk'])
                }
            }
        }

    }
}
