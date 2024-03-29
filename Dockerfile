FROM maven:3-jdk-11 as builder
COPY . /tmp/atlas
WORKDIR /tmp/atlas
RUN mvn package -DskipTests && mkdir /build && unzip /tmp/atlas/org.planqk.atlas.web/target/org.planqk.atlas.web.war -d /build/atlas

FROM ubuntu:18.04
LABEL maintainer = "Benjamin Weder <benjamin.weder@iaas.uni-stuttgart.de>"

ARG DOCKERIZE_VERSION=v0.6.1
ARG TOMCAT_VERSION=9.0.8

ENV POSTGRES_HOSTNAME localhost
ENV POSTGRES_PORT 5060
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_DB postgres

RUN apt-get -qq update && apt-get install -qqy software-properties-common openjdk-11-jdk wget

# setup tomcat
RUN mkdir /usr/local/tomcat
RUN wget --quiet --no-cookies https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tgz \
    && tar xzvf /tmp/tomcat.tgz -C /opt \
    && mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat \
    && rm /tmp/tomcat.tgz \
    && sed -i 's/port="8080"/port="6626"/g' /opt/tomcat/conf/server.xml
ENV CATALINA_HOME /opt/tomcat
ENV PATH $PATH:$CATALINA_HOME/bin

# install dockerize for configuration templating
RUN wget https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
    && rm dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz

RUN rm -rf ${CATALINA_HOME}/webapps/*
COPY --from=builder /build/atlas ${CATALINA_HOME}/webapps/atlas

EXPOSE 6626

# configure application with template and docker environment variables
ADD .docker/application.properties.tpl ${CATALINA_HOME}/webapps/application.properties.tpl

CMD dockerize -template ${CATALINA_HOME}/webapps/application.properties.tpl:${CATALINA_HOME}/webapps/atlas/WEB-INF/classes/application.properties \
    ${CATALINA_HOME}/bin/catalina.sh run
