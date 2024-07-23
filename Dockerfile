FROM ubuntu:22.04 AS build

RUN apt-get update &&  apt-get install -y \
    wget \
    openjdk-21-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*
    
COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

EXPOSE 8080

COPY --from=build /target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]