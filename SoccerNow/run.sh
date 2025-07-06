#!/usr/bin/env bash
set -euo pipefail

#############################################
# 1) Compilar el JAR localmente con Maven
#############################################
echo "🔨  Compilando el proyecto con Maven (sin tests)..."
mvn clean package -Dmaven.test.skip=true


#############################################
# 2) Bajar cualquier contenedor anterior
#############################################
echo "🐳  Deteniendo y borrando contenedores antiguos (si existen)..."
docker-compose down -v

#############################################
# 3) Construir la imagen Docker de la app
#############################################
echo "🐳  Construyendo imagen Docker de la aplicación..."
docker-compose build springbootapp

#############################################
# 4) Arrancar PostgreSQL + Spring Boot
#############################################
echo "🚀  Levantando contenedores con docker-compose..."
docker-compose up -d

echo "✅  ¡Todo levantado! Accede a http://localhost:8080"

#############################################
# 5) Mostrar logs de arranque de Java (últimos 10)
#############################################
sleep 3
echo "🌐  Últimos 10 renglones de logs de 'java_app':"
docker logs --tail 10 java_app
