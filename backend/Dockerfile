# Stage 1: Build environment
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /home/app

# 1. Cache dependencies: Copy only pom.xml first
# This prevents re-downloading all libraries if only your code changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime environment (The "Real" Image)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 3. Security: Run as a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 4. Copy only the JAR from the build stage
# Wildcard prevents failure if the version in pom.xml changes
COPY --from=build /home/app/target/*.jar app.jar

EXPOSE 8080

# 5. Optimize for container memory
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]