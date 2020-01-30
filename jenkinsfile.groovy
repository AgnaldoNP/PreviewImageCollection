pipeline {

//    agent {
//        label "agent any"
//    }

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
                        sh './gradlew clean test --no-daemon' //run a gradle task
                    } finally {
                        junit '**/build/test-results/test/*.xml'
                        //make the junit test results available in any case (success & failure)
                    }
                }
            }
        }

        stage('Frontend Unit Tests') {
            steps {
                sshagent(['git']) {
                    script {
                        try {
                            sh './gradlew cleanFrontendTest --no-daemon'
                            sh './gradlew frontendUnitTest --no-daemon'
                        } finally {
                            junit 'publicapi/frontend/test/karma-result.xml'
                        }
                    }
                }
            }
        }

        stage('Frontend Static Code Analysis') {
            steps {
                script {
                    try {
                        sh './gradlew tslint --no-daemon'
                    } finally { //Make checkstyle results available
                        checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: 'publicapi/frontend/tslint-result.xml', unHealthy: ''
                    }
                }
            }
        }

        stage('End 2 End Tests') {
            steps {
                script {
                    try {
                        sh './gradlew e2e --no-daemon'
                    } finally {
                        //Make selenium/protractor results available and publish the html (containing screenshots)
                        junit '**/e2e-results/junit-formatted/*.xml'
                        publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'publicapi/frontend/test/e2e-results/html-formatted/', reportFiles: 'htmlReport.html', reportName: 'End 2 End Test Report', reportTitles: ''])
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
