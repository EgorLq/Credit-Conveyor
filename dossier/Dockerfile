FROM openjdk:18.0.2.1-slim-buster
ARG JAR_FILE=*.jar
COPY ./target/${JAR_FILE} dossier.jar
ENTRYPOINT ["java", "-jar", "dossier.jar"]