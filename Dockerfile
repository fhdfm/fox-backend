FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y openjdk-21-jdk wget

# Instale o Maven
RUN wget https://www-us.apache.org/dist/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz && \
    tar xzvf apache-maven-3.8.5-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.8.5 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /opt/maven

# Adicione configurações do Maven
COPY settings.xml /root/.m2/settings.xml

# Baixe e instale o wkhtmltopdf
RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.5/wkhtmltox_0.12.5-1.focal_amd64.deb && \
    echo "deb http://security.ubuntu.com/ubuntu focal-security main" > /etc/apt/sources.list.d/focal-security.list && \
    apt-get update && \
    dpkg -i wkhtmltox_0.12.5-1.focal_amd64.deb || apt-get install -yf && \
    apt-get clean && \
    rm wkhtmltox_0.12.5-1.focal_amd64.deb

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

EXPOSE 8080

COPY --from=build /root/target/*.jar app.jar

ENTRYPOINT ["java", "-javaagent:/usr/local/newrelic/newrelic.jar", "-jar", "app.jar"]
