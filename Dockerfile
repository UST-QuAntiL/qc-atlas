FROM maven:3-jdk-8 as builder
COPY . /tmp/quality
WORKDIR /tmp/quality
RUN mvn package -DskipTests

FROM ubuntu:18.04
LABEL maintainer = "Benjamin Weder <benjamin.weder@iaas.uni-stuttgart.de>"

ENV TOMCAT_VERSION 9.0.8

RUN apt-get -qq update && apt-get install -qqy software-properties-common openjdk-8-jdk wget

# setup tomcat
RUN mkdir /usr/local/tomcat
RUN wget --quiet --no-cookies https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tgz && \
tar xzvf /tmp/tomcat.tgz -C /opt && \
mv /opt/apache-tomcat-${TOMCAT_VERSION} /opt/tomcat && \
rm /tmp/tomcat.tgz
ENV CATALINA_HOME /opt/tomcat
ENV PATH $PATH:$CATALINA_HOME/bin

# setup SWI prolog
RUN apt-get update && apt-get install -qqy swi-prolog swi-prolog-java
ENV SWI_HOME_DIR /usr/bin/swipl

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /tmp/quality/org.planqk.quality.war/target/org.planqk.quality.war.war ${CATALINA_HOME}/webapps/quality.war

EXPOSE 8080

CMD ["/opt/tomcat/bin/catalina.sh", "run"]