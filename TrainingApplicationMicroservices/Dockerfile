FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copy POM files relative to build context
COPY pom.xml .
COPY TrainingApplicationMicroservices/pom.xml TrainingApplicationMicroservices/pom.xml

# Remove unwanted modules from parent POM
RUN sed -i '/<module>TrainingWorkloadMicroservice\|DiscoveryServerMicroservice/d' pom.xml

# Install parent POM
RUN mvn -B -N install -DskipTests

# Copy source code
COPY TrainingApplicationMicroservices/src TrainingApplicationMicroservices/src

RUN rm -rf TrainingApplicationMicroservices/target

# Build with debug output
RUN mvn -B clean package \
    -pl TrainingApplicationMicroservices \
    -am \
    -DskipTests \
    -X

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/TrainingApplicationMicroservices/target/*.jar app.jar

# Add verification step
RUN jar tf app.jar | grep TrainingappApplication.class || exit 1

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]