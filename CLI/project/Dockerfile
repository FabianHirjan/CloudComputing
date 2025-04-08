# Stage 1: Build-ul frontend-ului cu Node
FROM node:18 as frontend-build
WORKDIR /app/frontend
# Copiem fișierele de configurare pentru instalarea dependențelor
COPY frontend/package*.json ./
# Instalăm dependențele
RUN npm install
# Copiem întregul cod al frontend-ului
COPY frontend/ ./
# Construim frontend-ul (se va genera directorul dist)
RUN npm run build

# Stage 2: Build-ul backend-ului cu Maven
FROM eclipse-temurin:17-jdk as backend-build
WORKDIR /app
# Copiem întregul folder al backend-ului (CCFramework) în container.
COPY CCFramework ./CCFramework
# Creăm (dacă nu există) directorul pentru resurse statice în care vom pune build-ul frontend.
RUN mkdir -p ./CCFramework/src/main/resources/static
# Copiem rezultatul build-ului frontend în resursele statice ale backend-ului.
COPY --from=frontend-build /app/frontend/dist ./CCFramework/src/main/resources/static/
# Asigurăm permisiunile pentru wrapper-ul Maven și rulăm build-ul
RUN chmod +x ./CCFramework/mvnw && \
    cd CCFramework && \
    ./mvnw clean package -DskipTests

# Stage 3: Imaginea finală de runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
# Copiem jar-ul rezultat din build-ul backend-ului
COPY --from=backend-build /app/CCFramework/target/*.jar app.jar
# Expunem portul configurat (8080)
EXPOSE 8080
# Rulăm aplicația Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
