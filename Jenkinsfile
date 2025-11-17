pipeline {
    agent none
    stages {
        stage('Build Jenkins') {
            steps {
                echo 'Building Jenkins'
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                echo 'Step 1: Starting Testing ...'
                sh './gradlew test'
            }
        }
        post {
            always {
                junit "build/reports/tests/test/*.xml"
                publishHTML{
                reportDir: "build/reports/tests/test",
                reportFiles: "index.html",
                reportName: "Test Report",
                alwaysLinkToLastBuild: true,
                allowMissing: true,
                keepAll: true
                }
            }
        }
    }
}