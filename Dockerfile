FROM ubuntu:22.04 AS build

RUN apt-get update
RUN apt-get install openjdk-21-jdk -y \
    wget 

ARG  jpeg=libjpeg-dev
ENV  CFLAGS=-w CXXFLAGS=-w
    
RUN apt-get update && apt-get install -y -q --no-install-recommends \
    dpkg-dev \
    libc6-dev \
    make \
    gcc-8 \
    g++-8 \
    libfontconfig1-dev \
    libfreetype6-dev \
    $jpeg \
    libpng-dev \
    libx11-dev \
    libxext-dev \
    libxrender-dev \
    python \
    zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*

RUN wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl3_3.0.2-0ubuntu1.1_amd64.deb && dpkg -i --ignore-depends=libc6 libssl3_3.0.2-0ubuntu1.1_amd64.deb
RUN wget http://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl-dev_3.0.2-0ubuntu1.1_amd64.deb && dpkg -i --ignore-depends=libc6 libssl-dev_3.0.2-0ubuntu1.1_amd64.deb
RUN update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-8 10 --slave /usr/bin/g++ g++ /usr/bin/g++-8 --slave /usr/bin/cpp cpp /usr/bin/cpp-8

# Baixe e instale o wkhtmltopdf
RUN  wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.1.4-2/wkhtmltox_0.12.1.4-2.stretch_amd64.deb && \
    apt install -y ./wkhtmltox_0.12.1.4-2.stretch_amd64.deb && \
    rm wkhtmltox_0.12.1.4-2.stretch_amd64.deb
#wget https://github.com/wkhtmltopdf/packaging/releases/download/0.12.6.1-2/wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
#    apt install -y ./wkhtmltox_0.12.6.1-2.jammy_amd64.deb && \
#    rm wkhtmltox_0.12.6.1-2.jammy_amd64.deb
    
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