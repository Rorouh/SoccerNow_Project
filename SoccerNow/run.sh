#!/usr/bin/env bash
set -euo pipefail

# 1) Compila el JAR localmente con Maven, usando tus dependencias ya descargadas
echo "🔨  Compilando con Maven (sin tests)..."
mvn clean package -DskipTests

echo "🐳  Borrando contenedores y volúmenes anteriores..."
docker-compose down -v

# 2) Construye la imagen Docker usando la red del host (para que el contenedor pueda resolver repo.maven.apache.org)
echo "🐳  Construyendo imagen Docker con --network host..."
docker build --network host -t myapp:latest .

# 3) Arranca los contenedores
echo "🚀  Levantando servicios con docker-compose..."
docker-compose up -d

echo "✅  ¡Listo! Proyecto desplegado en localhost:8080"
