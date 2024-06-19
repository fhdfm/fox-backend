FROM ubuntu:22.04 AS build

RUN apt-get update
RUN apt-get install -y --force-yes wget xvfb

# Baixar e instalar wkhtmltopdf manualmente 
RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.4/wkhtmltox-0.12.4_linux-generic-amd64.tar.xz 
RUN tar xvf wkhtmltox-0.12.4_linux-generic-amd64.tar.xz 
RUN mv wkhtmltox/bin/wkhtmltopdf /usr/bin 
RUN rm wkhtmltox-0.12.4_linux-generic-amd64.tar.xz  && rm -rf wkhtmltox 

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