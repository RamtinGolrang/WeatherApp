FROM gradle:7.6.1-jdk17 as build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./

COPY src/ ./src/

RUN gradle build --no-daemon

RUN ls -la build/libs/ && \
    unzip -q -c build/libs/*.jar META-INF/MANIFEST.MF || echo "No manifest found"

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 7070

LABEL description="Weather Application"

CMD ["java", "-jar", "app.jar"]