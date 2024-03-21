FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Can replace these with debian:12-slim if needed

FROM openjdk:17-jdk-slim AS java
COPY --from=build /target/donatedesk-0.0.1-SNAPSHOT.jar donatedesk.jar
COPY --from=build /target/classes/*.py .
COPY . .

FROM python:3.12-slim
COPY --from=java /donatedesk.jar .
COPY --from=java /*.py .
COPY --from=java /usr/local/openjdk-17 /usr/local/openjdk-17
ENV JAVA_HOME=/usr/local/openjdk-17
ENV JAVA_VERSION=17.0.2
ENV PATH=$JAVA_HOME/bin:$PATH
RUN python3 -m pip install --upgrade pip && \
    python3 -m pip install "pandas[excel]"
EXPOSE 8080
ENTRYPOINT ["java","-jar","donatedesk.jar"]