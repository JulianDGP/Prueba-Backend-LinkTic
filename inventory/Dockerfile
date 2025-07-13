# 1. Fase de build
FROM maven:3.9-amazoncorretto-21-alpine AS build
WORKDIR /src

# Copia el pom y descarga dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia código y compila omitiendo test
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Fase de runtime
FROM amazoncorretto:21-alpine
WORKDIR /src
COPY --from=build /src/target/inventory-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
