stage 'Checkout'
node() {
    deleteDir()
    checkout scm
}

stage 'Build & Archive Apk'
node('slave') {
    sh 'export ANDROID_SERIAL=0047758577 ; ./build.sh'
    step([$class: 'ArtifactArchiver', artifacts: 'meu_aplicativo/build/outputs/apk/meu_aplicativo.apk'])
}

