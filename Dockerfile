FROM ubuntu:latest AS build

# Atualize o sistema e instale dependências
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    wget \
    curl \
    maven \
    libxrender1 \
    libfontconfig1 \
    libjpeg-turbo8 \
    libxext6 \
    libx11-6 \
    fontconfig \
    xfonts-base \
    xfonts-75dpi

# Verifique a instalação do Maven
RUN mvn -v

# Baixe e instale o wkhtmltopdf
RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.5/wkhtmltox_0.12.5-1.focal_amd64.deb && \
    echo "deb http://security.ubuntu.com/ubuntu focal-security main" > /etc/apt/sources.list.d/focal-security.list && \
    apt-get update && \
    dpkg -i wkhtmltox_0.12.5-1.focal_amd64.deb || apt-get install -yf && \
    apt-get clean && \
    rm wkhtmltox_0.12.5-1.focal_amd64.deb

# Copie o código-fonte e o arquivo de configuração do Maven
COPY . .

# Instale as dependências do Maven e construa o projeto
RUN mvn clean install -DskipTests

# Imagem final
FROM openjdk:21-jdk-slim

# Crie o diretório para o New Relic e adicione os arquivos
RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

# Exponha a porta 8080
EXPOSE 8080

# Copie o arquivo JAR construído da fase anterior
COPY --from=build /app/target/*.jar app.jar

# Defina o comando de entrada
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]