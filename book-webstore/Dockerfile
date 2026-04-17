FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY .mvn ./.mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src ./src
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /opt/book-webstore

COPY --from=build /app/target/*.jar /opt/book-webstore/app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/book-webstore/app.jar"]
