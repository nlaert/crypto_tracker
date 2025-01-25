FROM maven:3.9.9-amazoncorretto-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Second stage: create a slim image
FROM amazoncorretto:17
COPY --from=build /app/target/tracker-1.0.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
