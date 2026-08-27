# ---------- build ----------
FROM maven:3.9-eclipse-temurin-25-noble AS builder
WORKDIR /app

# As dependencias mudam menos que o codigo: baixar antes de copiar o src faz o
# cache do Docker ser reaproveitado entre builds que so' alteram classes.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- runtime ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Rodar como root dentro do container e' privilegio desnecessario: se a aplicacao
# for comprometida, o atacante ja comeca com o maximo de permissoes.
RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

USER spring
EXPOSE 8080

# Deixa a JVM respeitar os limites de memoria do container.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
