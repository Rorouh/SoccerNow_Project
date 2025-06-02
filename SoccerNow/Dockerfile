# Etapa de compilación (builder)
FROM maven:latest AS builder
WORKDIR /build

# Sólo copiamos el pom y bajamos dependencias para cachear correctamente
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del proyecto y compilamos sin tests
COPY . .
RUN mvn clean package -DskipTests

# Etapa de ejecución (runtime)
FROM eclipse-temurin:17
# Creamos directorios necesarios (sin declarar VOLUME)
RUN mkdir -p /tmp /app /app/logs
WORKDIR /app

# Copiamos el JAR desde el builder (ajusta el nombre según tu versión)
COPY --from=builder /build/target/soccernow-1.0.jar ./app.jar

# Puerto expuesto por la aplicación
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
