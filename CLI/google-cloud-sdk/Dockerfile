# ---------- STAGE 1: Build frontend ----------
FROM node:18 AS frontend-builder
WORKDIR /app
COPY frontend/ ./
RUN npm install
RUN npm run build

# ---------- STAGE 2: Build backend ----------
FROM eclipse-temurin:17-jdk AS backend-builder
WORKDIR /app
COPY backend/ ./
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# ---------- STAGE 3: Create final image ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy backend JAR
COPY --from=backend-builder /app/target/*.jar app.jar

# Copy built frontend files into Spring Boot's static folder
COPY --from=frontend-builder /app/dist/ /app/static/

# Expune portul folosit de Spring
EXPOSE 8080

# Setare ENV pentru port (Cloud Run folosește variabila PORT)
ENV PORT=8080

# Rulează aplicația
ENTRYPOINT ["java", "-jar", "app.jar"]
