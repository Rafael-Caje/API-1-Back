# Estágio 1: Compilar o aplicativo Spring Boot
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Configurar o contêiner com Java e Python
FROM openjdk:17-jdk-slim

# Instalar Python e dependências
RUN apt-get update && \
    apt-get install -y python3 python3-pip git && \
    pip3 install --no-cache-dir -r /app/requirements.txt

# Copiar o arquivo JAR do Spring Boot
COPY --from=build /app/target/*.jar /scraping-service.jar

# Copiar o código do scraper Python
COPY . /scraper

# Definir o diretório de trabalho
WORKDIR /scraper

# Configurar o comando para iniciar o aplicativo
CMD ["sh", "-c", "java -jar /scraping-service.jar & python3 app.py"]
