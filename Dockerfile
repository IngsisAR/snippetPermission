# Etapa 1: Construcción
FROM gradle:8.8-jdk21 AS build

LABEL author="Ingsis AHRE"

COPY  . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build --no-daemon

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre

COPY --from=build /home/gradle/src/build/libs/*.jar /app/snippetPermission.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.jar /newrelic.jar
COPY --from=build /home/gradle/src/newrelic/newrelic.yml /newrelic.yml

WORKDIR /app
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=production", "-javaagent:/newrelic.jar", "-Dnewrelic.config.license_key=${NEW_RELIC_LICENSE_KEY}", "-Dnewlic.config.app_name=${NEW_RELIC_APP_NAME}", "/app/snippetPermission.jar"]
