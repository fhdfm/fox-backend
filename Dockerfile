# Etapa de build
FROM ubuntu:22.04 AS build

# Atualizar pacotes e instalar dependências
RUN apt-get update && apt-get install -y \
    wget \
    openjdk-21-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Copiar os arquivos do projeto
COPY . .

# Construir o projeto
RUN mvn clean install -DskipTests

# Etapa de execução
FROM openjdk:21-jdk-slim

# Criar diretório para o New Relic
RUN mkdir -p /usr/local/newrelic

# Adicionar o New Relic agent e configuração
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

# Expor a porta da aplicação
EXPOSE 8080

# Copiar o jar gerado na etapa de build
COPY --from=build /target/*.jar app.jar

# Definir o profile ativo do Spring Boot
ENV SPRING_PROFILES_ACTIVE=prod

# Configurar parâmetros da JVM para otimizar memória e desempenho
ENTRYPOINT ["java", 
            "-Xms256m", 
            "-Xmx1536m", 
            "-XX:+UseG1GC", 
            "-XX:MaxGCPauseMillis=200", 
            "-XX:ParallelGCThreads=4", 
            "-XX:ConcGCThreads=2", 
            "-XX:-UseSharedSpaces", 
            "-javaagent:/usr/local/newrelic/newrelic.jar", 
            "-jar", 
            "app.jar"]
