FROM gradle:8.10.1-jdk21 AS build
ARG BUILD_HOME=/server
COPY --chown=gradle:gradle . $BUILD_HOME/

WORKDIR $BUILD_HOME/

RUN gradle build -x test #comment after build to crash(((

FROM eclipse-temurin:21-jre-alpine

ARG BUILD_HOME=/server
WORKDIR /app

COPY --from=build $BUILD_HOME/build/libs/*.jar /app/os-test-0.0.1-SNAPSHOT-plain.jar

RUN apk update && apk add bash && apk add --no-cache coreutils
COPY wait-for-it.sh /usr/local/bin/wait-for-it
RUN chmod +x /usr/local/bin/wait-for-it

CMD ["java", "-jar", "/app/os-test-0.0.1-SNAPSHOT-plain.jar"]