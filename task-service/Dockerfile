FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
COPY target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]