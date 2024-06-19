FROM ubuntu:22.04 AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y \
    wget \
    maven

# Baixe e instale o wkhtmltopdf
RUN wget https://github.com/fhdfm/wkhtmltopdf/blob/main/wkhtmltox_0.12.6-1.jammy_amd64.deb && \
    apt install -y ./wkhtmltox_0.12.6-1.jammy_amd64.deb && \
    rm wkhtmltox_0.12.6-1.jammy_amd64.deb
    
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