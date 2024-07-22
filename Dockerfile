# Estágio 1: Compilar o aplicativo
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Executar o aplicativo em um contêiner
FROM openjdk:17-alpine
COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/application.properties /app/application.properties 
ENTRYPOINT ["java", "-jar", "/app.jar"]


