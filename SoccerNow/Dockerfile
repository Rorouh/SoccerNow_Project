# Etapa de compilación: usamos la imagen oficial de Maven + JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos pom.xml y la carpeta src/
COPY pom.xml .
COPY src ./src

# Compilamos el JAR (sin tests para agilizar)
RUN mvn clean package -Dmaven.test.skip=true

# Etapa de runtime: usamos OpenJDK 17 JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el JAR generado por la etapa de build
COPY --from=build /app/target/soccernow-1.0.jar ./soccernow.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando por defecto
ENTRYPOINT ["java","-jar","/app/soccernow.jar"]
