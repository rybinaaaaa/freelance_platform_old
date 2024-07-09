FROM jelastic/maven:3.9.5-openjdk-21 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:23-ea-jdk-oracle

WORKDIR /app
COPY --from=build /app/target/Application-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]