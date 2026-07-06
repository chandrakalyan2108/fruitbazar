pipeline {
    agent any
    environment {
        USERNAME = "ubuntu"
        SERVER_IP = credentials('fruitbazar-server-ip')
        APP_NAME = "fruitbazar"
        TOMCAT_HOME = "/opt/tomcat"
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
                    set -ex

                    rm -rf ~/fruitbazar
                    git clone https://github.com/chandrakalyan2108/fruitbazar.git ~/fruitbazar
                    cd ~/fruitbazar
                    mvn clean package

                    # Confirm the build actually produced a WAR before touching Tomcat
                    ls -la target/*.war

                    # Confirm Tomcat's webapps dir actually exists and is a directory
                    if [ ! -d ${TOMCAT_HOME}/webapps ]; then
                        echo \\\"ERROR: ${TOMCAT_HOME}/webapps is not a directory on this server.\\\"
                        echo \\\"Actual TOMCAT_HOME contents:\\\"
                        ls -la ${TOMCAT_HOME} || echo \\\"${TOMCAT_HOME} does not exist at all\\\"
                        exit 1
                    fi

                    sudo rm -rf ${TOMCAT_HOME}/webapps/${APP_NAME}
                    sudo rm -f ${TOMCAT_HOME}/webapps/${APP_NAME}.war
                    sudo cp target/FruitBazar.war ${TOMCAT_HOME}/webapps/${APP_NAME}.war

                    sudo ${TOMCAT_HOME}/bin/shutdown.sh || true
                    sleep 5
                    sudo ${TOMCAT_HOME}/bin/startup.sh
                "
                '''
            }
        }
    }
}
