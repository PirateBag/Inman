# This docker file is not invoked directly, but through
# mvn install docker:build

FROM alpine:3.12.1

LABEL maintainer="Sovos support@sovos.com"

# Version which locates the built jar
ARG VERSION=0.0.1-SNAPSHOT
ARG ARTIFACT_ID=ng-tjc-service

ENV VERSION $VERSION
ENV ARTIFACT_ID $ARTIFACT_ID

# Java memory args
ENV XMS_MEMORY=4G
ENV XMX_MEMORY=20G

#ENV JAVA_OPTS="$JAVA_OPTS -Xms$XMS_MEMORY -Xmx$XMX_MEMORY"

### java 11 can use --no-cache
RUN apk add openjdk11

VOLUME /tmp
#RUN mkdir /opt/ng-tjc-service
COPY maven/${ARTIFACT_ID}-${VERSION}.jar /opt/ng-tjc-service/ng-tjc-service.jar
RUN sh -c 'touch /opt/ng-tjc-service/ng-tjc-service.jar'

COPY entrypoint.sh /opt/entrypoint.sh
RUN chmod +x /opt/entrypoint.sh
RUN dos2unix /opt/entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/opt/entrypoint.sh"]
