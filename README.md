````markdown
# SoccerNow: Gestión de Partidos de Futsal

> **Este archivo debe ser reescrito** con base en la información descrita en el enunciado del proyecto.

---

## Miembros

- André Santos – fc57538 → C2  
- Erickson Cacondo – fc53653 → C3  
- Miguel Sánchez – fc64675 → C1  

---

## Proyecto Práctico #1

- El proyecto debe desarrollarse en grupos de **máximo tres alumnos**.  
- En el archivo `README.md` deben aparecer los integrantes del grupo, su número y qué conjuntos de casos de uso resolverá cada alumno.

### Plagio

- Además de inspección manual, se utilizará un software de detección de plagio en el código fuente del proyecto.  
- **Todos** los alumnos que entreguen código plagiado tendrán su proyecto anulado.

### Uso de Inteligencia Artificial

- Está permitido usar herramientas de IA como ChatGPT, Copilot, DeepSeek y similares.  
- Sin embargo, todos los miembros del grupo deben ser capaces de **comprender y explicar** el proyecto **en su totalidad** a los docentes cuando se les solicite. Si existe código que el grupo no pueda explicar, el proyecto se considerará **plagio**.

### Submisión

- La fecha de entrega es **01/05/2025** a las 23:59.  
- La descripción del proceso de entrega figura en el enunciado del proyecto.

---

## Tareas de la Fase 1

El equipo deberá:

1. Hacer *fork* del repositorio original  
   `https://git.alunos.di.fc.ul.pt/css000/soccernow`  
   a la cuenta de uno de los miembros del grupo.  
2. Dar acceso a todos los miembros como **Maintainer**.  
3. Dar acceso a la cuenta **css000** como **Reporter**.  
4. Modificar `README.md` para identificar al equipo e indicar qué conjunto de casos de uso implementará cada miembro.  
5. Crear una carpeta `docs` que contenga un único PDF con todos los diagramas del proyecto.  
6. Dibujar el modelo de dominio, considerando todos los casos de uso, y colocarlo en `docs`.  
7. Diseñar el diagrama de secuencia (SSD) para el caso de uso **H**.  
8. Esbozar el diagrama de clases que muestre la división en capas.  
9. Anotar las clases relevantes con las anotaciones JPA para asegurar el mejor mapeo posible.  
10. Justificar en el informe cada decisión tomada en el mapeo JPA.  
11. Incluir en el informe las garantías que ofrece el sistema respecto a la lógica de negocio.  
12. Generar la base de datos a partir de las anotaciones.  
13. Implementar tests que aseguren la corrección de la lógica de negocio.  
14. Implementar los endpoints REST necesarios (accesibles vía Swagger) para los casos de uso implementados.

---

## Cómo Entregar

Para entregar el trabajo, crea una etiqueta (tag) llamada `fase1` y envíala al repositorio. Debe incluir:

- El código fuente del proyecto.  
- Los archivos necesarios para ejecutar el proyecto en Docker.  
- Un único PDF en la carpeta `docs` con todos los diagramas.

Ejecuta:

```bash
git tag fase1
git push origin fase1
````

### Atención

Confirma que el proyecto es accesible a la cuenta **CSS000** en la etiqueta `fase1`. En caso contrario, la entrega recibirá un **0**. Asegúrate de que el proyecto se ejecuta sin errores de compilación ni otros impedimentos en el entorno Docker.

---

## FAQ

### ¿Necesito `sudo` para ejecutar `run.sh`?

Prueba con:

```bash
sudo usermod -aG docker $USER
```

Luego cierra sesión y vuelve a entrar.
O consulta [esta solución](https://www.digitalocean.com/community/questions/how-to-fix-docker-got-permission-denied-while-trying-to-connect-to-the-docker-daemon-socket).

---

### Docker no se instala en Ubuntu

Prueba [esta solución](https://askubuntu.com/a/1411717).

---

### `run.sh` no funciona en macOS M1

Ejecuta:

```bash
docker ps
```

Si sigue fallando, prueba [esta solución](https://stackoverflow.com/a/68202428/28516).
También asegúrate de tener Docker Desktop (`brew install --cask docker`) y no sólo la herramienta de línea de comandos (`brew install docker`). La aplicación Docker debe estar corriendo (ícono en la barra de menús).

---

### En Windows, `bash setup.sh` no funciona

Ejecuta en una Bash (Git Bash, MSys2 bash o WSL), **no** en PowerShell ni en CMD.exe.

---

### En Windows, tengo Python pero `bash setup.sh` no encuentra `pip`

Añade `pip` al `PATH`. Busca en:

```
C:\Users\<tu_usuario>\AppData\Local\Programs\Python\<versión>\Scripts
```

(Fíjate en que la carpeta puede estar oculta.)

---

### `docker compose` no funciona

`docker compose` es el comando de la versión más reciente de Docker.
`docker-compose` corresponde a la versión antigua. Actualiza Docker.

---

### Al ejecutar `bash run.sh` obtengo error 401 al descargar imágenes

En el terminal, haz logout y login de Docker:

```bash
docker logout
docker login
```

```
```
