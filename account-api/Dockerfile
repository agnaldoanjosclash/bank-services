FROM openjdk:17-jdk-alpine3.13
COPY target/*.jar /app.jar
ENV SPRING_PROFILES_ACTIVE prod
CMD ["java", "-jar", "/app/app.jar"]
