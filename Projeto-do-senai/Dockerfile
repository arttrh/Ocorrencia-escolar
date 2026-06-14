FROM maven:3.9-eclipse-temurin-25-noble AS builder
WORKDIR /app
COPY pom.xml .
COPY /src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
CMD ["java","-jar","app.jar"]