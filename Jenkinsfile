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
    }
}