pipeline {
    agent any
    environment {
        USERNAME    = "ubuntu"
        SERVER_IP   = credentials('fruitbazar-server-ip')
        APP_NAME    = "fruitbazar"
        TOMCAT_HOME = "/home/ubuntu/tomcat"
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

                    # Confirm Tomcat webapps dir exists
                    if [ ! -d ${TOMCAT_HOME}/webapps ]; then
                        echo \\\"ERROR: ${TOMCAT_HOME}/webapps is not a directory on this server.\\\"
                        ls -la ${TOMCAT_HOME} || echo \\\"${TOMCAT_HOME} does not exist at all\\\"
                        exit 1
                    fi

                    # Confirm bin scripts are executable as root
                    sudo test -x ${TOMCAT_HOME}/bin/shutdown.sh || { echo \\\"ERROR: shutdown.sh not executable\\\"; exit 1; }
                    sudo test -x ${TOMCAT_HOME}/bin/startup.sh  || { echo \\\"ERROR: startup.sh not executable\\\"; exit 1; }

                    sudo rm -rf ${TOMCAT_HOME}/webapps/${APP_NAME}
                    sudo rm -f ${TOMCAT_HOME}/webapps/${APP_NAME}.war

                    WAR_FILE=\\$(ls target/*.war | head -n1)
                    sudo cp \\$WAR_FILE ${TOMCAT_HOME}/webapps/${APP_NAME}.war

                    # Stop Tomcat cleanly
                    sudo ${TOMCAT_HOME}/bin/shutdown.sh || true
                    sleep 5

                    # Kill anything still bound to port 8080 (NOT a broad 'pkill -f catalina',
                    # which matches the deploy shell's own command line -- e.g. catalina.out in
                    # this very script -- and kills the SSH session running the pipeline itself)
                    sudo fuser -k 8080/tcp || true
                    sleep 2

                    # Start Tomcat as root consistently (matches sudo used above,
                    # avoids permission mismatches between root-owned and ubuntu-owned PID/log files)
                    sudo ${TOMCAT_HOME}/bin/startup.sh

                    # Wait for the app to actually respond instead of assuming success
                    echo 'Waiting for Tomcat to deploy the app...'
                    DEPLOYED=false
                    for i in \\$(seq 1 15); do
                        sleep 3
                        STATUS=\\$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/${APP_NAME}/ || echo '000')
                        echo \\\"Attempt \\$i: HTTP \\$STATUS\\\"
                        if [ \\\"\\$STATUS\\\" = \\\"200\\\" ] || [ \\\"\\$STATUS\\\" = \\\"302\\\" ]; then
                            DEPLOYED=true
                            break
                        fi
                    done

                    if [ \\\"\\$DEPLOYED\\\" != \\\"true\\\" ]; then
                        echo \\\"ERROR: App did not come up after deploy. Last 100 lines of catalina.out:\\\"
                        tail -100 ${TOMCAT_HOME}/logs/catalina.out
                        exit 1
                    fi

                    echo \\\"SUCCESS: ${APP_NAME} is responding on port 8080\\\"
                "
                '''
            }
        }
    }
}
