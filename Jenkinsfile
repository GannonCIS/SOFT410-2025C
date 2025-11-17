pipeline {
    agent any
    stages {
        stage('Build Jenkins') {
            steps {
                sh 'chmod +x ./gradlew'
                echo 'Building Jenkins'
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                echo 'Step 1: Starting Testing ...'
                sh './gradlew test --rerun-tasks'
            }
            post {
                always {
                  junit "build/reports/test/*.xml"
                  publishHTML([
                  reportDir: "build/reports/tests/test",
                  reportFiles: "index.html",
                  reportName: "Test Report",
                  alwaysLinkToLastBuild: true,
                  allowMissing: true,
                  keepAll: true
                  ])
                }
            }
        }
    }
}