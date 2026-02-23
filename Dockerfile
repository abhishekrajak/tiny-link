# ---------- Build stage ----------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# copy only pom first (better caching)
COPY pom.xml .
COPY mvnw .

RUN chmod +x ./mvnw

# now copy source
COPY src src

RUN ./mvnw clean package -DskipTests


# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
