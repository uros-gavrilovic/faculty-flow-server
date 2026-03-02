FROM eclipse-temurin:25-jre
WORKDIR /app
COPY ./target/*.jar app.jar
EXPOSE 8080
LABEL name="faculty-flow-room"
ENTRYPOINT ["java","-jar","/app/app.jar"]
