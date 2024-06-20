FROM ubuntu:22.04 AS build

RUN apt-get update &&  apt-get install -y \
    wget \
    openjdk-21-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Baixe e instale libjpeg-turbo8
RUN wget http://mirrors.kernel.org/ubuntu/pool/main/libj/libjpeg-turbo/libjpeg-turbo8_2.1.2-0ubuntu1_amd64.deb && \
    apt-get update && \
    apt-get install -y ./libjpeg-turbo8_2.1.2-0ubuntu1_amd64.deb && \
    cp /usr/lib/x86_64-linux-gnu/libjpeg.so.8 /usr/local/lib/ && \
    rm libjpeg-turbo8_2.1.2-0ubuntu1_amd64.deb && \
    rm -rf /var/lib/apt/lists/*

# Baixe e instale wkhtmltox
RUN wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6.1-2/wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    apt-get update && \
    dpkg -i wkhtmltox_0.12.6.1-2.jammy_amd64.deb || apt-get -f install -y && \
    rm wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
    rm -rf /var/lib/apt/lists/*
    
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