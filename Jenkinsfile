pipeline {
    agent any
    environment {
        USERNAME = "ubuntu"
        SERVER_IP = credentials('fruitbazar-server-ip')
        APP_NAME = "fruitbazar"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build & Deploy') {
            steps {
                sh '''
                ssh -o StrictHostKeyChecking=no ${USERNAME}@${SERVER_IP} "
                    set -e
                    rm -rf ~/fruitbazar
                    git clone https://github.com/YOUR_GITHUB_USERNAME/fruitbazar.git ~/fruitbazar
                    cd ~/fruitbazar
                    mvn clean package
                    sudo rm -rf /opt/tomcat/webapps/${APP_NAME}
                    sudo rm -f /opt/tomcat/webapps/${APP_NAME}.war
                    sudo cp target/*.war /opt/tomcat/webapps/${APP_NAME}.war
                    sudo /opt/tomcat/bin/shutdown.sh || true
                    sleep 5
                    sudo /opt/tomcat/bin/startup.sh
                "
                '''
            }
        }
    }
}
