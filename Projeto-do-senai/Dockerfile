FROM maven:3.9-eclipse-temurin-25-noble AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 app
COPY --from=builder /app/target/*.jar app.jar
USER app
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:ActiveProcessorCount=2"
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
