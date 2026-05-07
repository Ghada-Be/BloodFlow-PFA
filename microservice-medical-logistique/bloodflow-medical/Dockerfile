# ============================================================
# Dockerfile — BloodFlow Medical Service
# ============================================================

# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S bloodflow && adduser -S bloodflow -G bloodflow

COPY --from=builder /app/target/*.jar app.jar
RUN chown bloodflow:bloodflow app.jar

USER bloodflow
EXPOSE 8083

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
