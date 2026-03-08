FROM maven:3.9.11-eclipse-temurin-21 AS test

WORKDIR /workspace

# Copy Maven descriptors first to maximize layer cache reuse.
COPY backend/pom.xml backend/pom.xml

# Prime dependency cache before copying full source tree.
RUN mvn -f backend/pom.xml -B -DskipTests dependency:go-offline

COPY backend/src backend/src
COPY backend/checkstyle.xml backend/checkstyle.xml

CMD ["mvn", "-f", "backend/pom.xml", "-B", "test"]
