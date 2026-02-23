# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# copy only pom first (better caching)
COPY pom.xml .

# download dependencies (better caching)
RUN mvn dependency:go-offline

# now copy source
COPY src src

RUN mvn clean package -DskipTests


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
