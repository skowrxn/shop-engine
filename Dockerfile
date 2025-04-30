FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar shop-engine-1.0-beta.jar
ENTRYPOINT ["java", "-jar", "shop-engine-1.0-beta.jar"]
