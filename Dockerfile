FROM ubuntu:latest AS build

# Atualiza os repositórios e instala pacotes necessários
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    maven \
    libfreetype6 \
    libfontconfig1 \
    ca-certificates \
    && update-ca-certificates

# Configurações de segurança para Maven
RUN mkdir -p /usr/share/maven/conf
COPY settings.xml /usr/share/maven/conf/settings.xml

# Copia o código fonte para a imagem
COPY . .

# Compila o projeto Maven
RUN mvn clean install -DskipTests

FROM ubuntu:latest

# Atualiza os repositórios e instala dependências do wkhtmltopdf
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libfontconfig1 \
    fontconfig \
    xz-utils \
    wget \
    libjpeg-turbo8 \
    libx11-6 \
    libxext6 \
    libxrender1 \
    xfonts-75dpi \
    xfonts-base \
    libssl3 \
    ca-certificates \
    && update-ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Baixa e instala o wkhtmltopdf
RUN wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6.1-2/wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    dpkg -i wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    apt-get install -f -y && \
    rm wkhtmltox_0.12.6.1-2.jammy_amd64.deb

# Cria diretório e adiciona arquivos New Relic
RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

# Expõe a porta 8080
EXPOSE 8080

# Copia o artefato gerado na fase de build
COPY --from=build /target/*.jar app.jar

# Define o comando de entrada
ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]
