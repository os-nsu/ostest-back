FROM gradle:8.10.1-jdk21 AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
ARG CACHE_CODE=/home/gradle/java-code
ARG LIQUIBASE_DIR=src/main/resources/liquibase-mig
RUN mkdir -p $CACHE_CODE/$LIQUIBASE_DIR
COPY --chown=gradle:gradle build.gradle $CACHE_CODE
COPY --chown=gradle:gradle ./$LIQUIBASE_DIR/liquibase.properties $CACHE_CODE/$LIQUIBASE_DIR/liquibase.properties

WORKDIR $CACHE_CODE
RUN gradle dependencies --no-daemon --build-cache

FROM gradle:8.10.1-jdk21 AS build
ARG BUILD_HOME=/server
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . $BUILD_HOME/
WORKDIR $BUILD_HOME/
RUN gradle build -i --stacktrace -x test --build-cache

FROM eclipse-temurin:21-jre-alpine

ARG BUILD_HOME=/server
WORKDIR /app

COPY --from=build $BUILD_HOME/build/libs/*.jar /app/os-test-0.0.1-SNAPSHOT-plain.jar

CMD ["java", "-jar", "/app/os-test-0.0.1-SNAPSHOT-plain.jar"]
