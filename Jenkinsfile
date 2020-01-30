stage 'Checkout'
node() {
    deleteDir()
    checkout scm
}

stage 'Build & Archive Apk'
node() {
    sh 'export ANDROID_SERIAL=0047758577 ; ./gradlew assembleDebug'
    step([$class: 'ArtifactArchiver', artifacts: 'app/build/outputs/apk/debug/app-debug.apk'])
}

