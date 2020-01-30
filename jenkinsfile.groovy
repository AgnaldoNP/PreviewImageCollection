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
                    sh 'export ANDROID_SERIAL=0047758577 ; ./gradlew build ; ls'
//                    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/debug/app-debug.apk'])

                }
            }
        }

    }
}
