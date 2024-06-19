FROM ubuntu:22.04 AS build

# Definir variáveis de ambiente para evitar interação durante a instalação
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

# Adicionar repositório para bionic-security e a chave pública GPG
RUN apt-get update && apt-get install -y gnupg \
    && echo "deb http://security.ubuntu.com/ubuntu bionic-security main" > /etc/apt/sources.list.d/bionic-security.list \
    && apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 3B4FE6ACC0B21F32

# Atualizar lista de pacotes
RUN apt-get update

# Instalar dependências necessárias
RUN apt-get install -y \
    wget \
    libxrender1 \
    libfontconfig1 \
    libjpeg62-turbo \
    libxext6 \
    xfonts-base \
    xfonts-75dpi \
    libssl1.0-dev

# Instalar wkhtmltopdf
RUN apt-get install -y wkhtmltopdf

# Limpar cache do apt
RUN rm -rf /var/lib/apt/lists/*
    
COPY . .

RUN apt-get install maven -y
RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

EXPOSE 8080

COPY --from=build /usr/local/bin/wkhtmltopdf /usr/local/bin/wkhtmltopdf
COPY --from=build /target/*.jar app.jar

ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]