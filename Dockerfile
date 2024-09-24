FROM gradle:8.10.1-jdk21 AS build
ARG BUILD_HOME=/server
COPY --chown=gradle:gradle . $BUILD_HOME/

WORKDIR $BUILD_HOME/

RUN gradle build

FROM eclipse-temurin:21-jre-alpine

ARG BUILD_HOME=/server
WORKDIR /app

COPY --from=build $BUILD_HOME/build/libs/*.jar /app/os-test-0.0.1-SNAPSHOT-plain.jar

CMD ["java", "-jar", "/app/os-test-0.0.1-SNAPSHOT-plain.jar"]