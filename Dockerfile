FROM ubuntu:22.04 AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y \
    wget \
    maven
    
RUN wget http://archive.ubuntu.com/ubuntu/pool/main/libj/libjpeg-turbo/libjpeg-turbo8-dev_2.1.2-0ubuntu1_amd64.deb && \
    apt install -y ./libjpeg-turbo8-dev_2.1.2-0ubuntu1_amd64.deb && \
    rm libjpeg-turbo8-dev_2.1.2-0ubuntu1_amd64.deb

# Baixe e instale o wkhtmltopdf
RUN wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6.1-2/wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    apt install -y ./wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    rm wkhtmltox_0.12.6.1-2.jammy_amd64.deb
    
COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

EXPOSE 8080

COPY --from=build /usr/local/bin/wkhtmltopdf /usr/local/bin/wkhtmltopdf
COPY --from=build /target/*.jar app.jar

ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]