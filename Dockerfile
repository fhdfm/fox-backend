FROM ubuntu:22.04 AS build

# Instalar wkhtmltopdf
RUN apt-get install -y wkhtmltopdf
    
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