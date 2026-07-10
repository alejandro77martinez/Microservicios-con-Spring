pipeline {
  agent any

  tools {
    // Usamos tu Java 21 instalado en Ubuntu
    jdk 'Java21'
  }

  // DEFINIMOS EL PARÁMETRO: será un desplegable con los 5 proyectos
  parameters {
    choice(
      name: 'PROJECT_NAME',
      choices: ['api-gateway', 'auth-service', 'config-server', 'project-service', 'task-service'],
      description: 'Selecciona el proyecto Spring Boot que quieres construir'
    )
  }

  environment {
    BASE_PATH = '/home/neil/springMicroservices'  // Ajusta según tu ruta
    // Umbrales de cobertura (ajústalos a tu gusto)
    MIN_INSTRUCTION = '80'
    MIN_BRANCH = '70'
    MIN_LINE = '80'
  }

  stages {
    // 1. Test y Quality Gate (solo para el proyecto elegido)
    stage('Test & Quality Gate') {
      steps {
        // Cambiamos al directorio del proyecto seleccionado
        dir("${env.BASE_PATH}/${params.PROJECT_NAME}") {
          sh './mvnw clean verify'       // Ejecutamos pruebas
        }
      }
      post {
        always {
          script {
            if (currentBuild.currentResult != 'FAILURE') {
              dir("${env.BASE_PATH}/${params.PROJECT_NAME}") {
                // Publicamos resultados de pruebas unitarias
                junit 'target/surefire-reports/*.xml'

                // Quality Gate con JaCoCo
                recordCoverage(
                  tools: [[parser: 'JACOCO', pattern: 'target/site/jacoco/jacoco.xml']],
                  qualityGates: [
                    [threshold: 80, metric: 'INSTRUCTION', criticality: 'UNSTABLE'],
                    [threshold: 70, metric: 'BRANCH',      criticality: 'UNSTABLE'],
                    [threshold: 80, metric: 'LINE',        criticality: 'UNSTABLE'],
                    [threshold: 80, metric: 'METHOD',      criticality: 'UNSTABLE']
                  ]
                )
              }
            }
          }
        }
      }
    }

    // 2. Análisis en SonarQube (solo si las pruebas no fallaron)
    stage('SonarQube Analysis') {
      when {
        expression {
          currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE'
        }
      }
      steps {
        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          dir("${env.BASE_PATH}/${params.PROJECT_NAME}") {
            sh '''
              mkdir -p target/sonar
              chmod -R u+rwX target/sonar
              ./mvnw sonar:sonar -Dsonar.token="$SONAR_TOKEN"
            '''
          }
        }
      }
    }

    // 3. Construcción de imagen Docker y publicación en Docker Hub
    stage('Docker Build & Publish') {
      when {
        expression {
          currentBuild.currentResult == null || currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'UNSTABLE'
        }
      }
      environment {
        PROJECT_NAME = "${params.PROJECT_NAME}"
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
          dir("${env.BASE_PATH}/${params.PROJECT_NAME}") {
            sh '''
              echo "$DOCKER_HUB_PASSWORD" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin
              docker build -t "$DOCKER_HUB_USERNAME/$PROJECT_NAME:latest" .
              docker push "$DOCKER_HUB_USERNAME/$PROJECT_NAME:latest"
            '''
          }
        }
      }
    }
  }

  post {
    always {
      cleanWs()  // Limpia el workspace al terminar
    }
    failure {
      echo "El proyecto ${params.PROJECT_NAME} falló en pruebas o cobertura."
    }
  }
}