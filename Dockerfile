FROM ubuntu:latest AS build

RUN apt-get update && apt-get install -y openjdk-21-jdk wget curl

# Instale o Maven
RUN mkdir -p /opt/maven && \
    curl -fSL https://archive.apache.org/dist/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz -o apache-maven-3.8.5-bin.tar.gz && \
    tar xzvf apache-maven-3.8.5-bin.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.8.5 /opt/maven && \
    ln -s /opt/maven/bin/mvn /usr/bin/mvn && \
    rm apache-maven-3.8.5-bin.tar.gz

ENV MAVEN_HOME /opt/maven
ENV PATH $MAVEN_HOME/bin:$PATH

# Verificar se o Maven foi instalado corretamente
RUN echo $MAVEN_HOME
RUN echo $PATH
RUN ls -l /opt/maven/bin
RUN mvn -v

# Adicione configurações do Maven
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/settings.xml

# Baixe e instale o wkhtmltopdf
RUN wget https://github.com/wkhtmltopdf/wkhtmltopdf/releases/download/0.12.5/wkhtmltox_0.12.5-1.focal_amd64.deb && \
    echo "deb http://security.ubuntu.com/ubuntu focal-security main" > /etc/apt/sources.list.d/focal-security.list && \
    apt-get update && \
    apt-get install -y libxrender1 libfontconfig1 libjpeg-turbo8 libxext6 libx11-6 fontconfig xfonts-base xfonts-75dpi && \
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
