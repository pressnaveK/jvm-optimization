FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q package
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /app/diagnostics
COPY --from=build /src/target/jvm-optimization-1.0.0.jar app.jar
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:NativeMemoryTracking=summary -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/diagnostics -Xlog:gc*,safepoint:file=/app/diagnostics/gc.log:time,uptime,level,tags:filecount=5,filesize=20M -XX:StartFlightRecording=name=lab,settings=profile,disk=true,maxage=30m,maxsize=256m,dumponexit=true,filename=/app/diagnostics/app.jfr"
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=lab"]