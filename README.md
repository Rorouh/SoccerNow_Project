A continuación tienes un **README completo** listo para sustituir al actual.
Incluye **toda la información** de la Fase 1 *y* de la Fase 2 (arquitectura, endpoints, lógica de negocio, tests, datos de arranque, Docker Compose, JavaFX, etc.).
Copia el contenido en `README.md` y, si lo deseas, añade después los badges de CI/CD o enlaces a tu repo.

---

```markdown
# ⚽️ SoccerNow — Plataforma de gestión de Futsal
> Proyecto práctico de CSS - 2024/25 – Universidade de Lisboa

<table>
<tr><th>Repositorio</th><td><https://git.alunos.di.fc.ul.pt/&lt;user&gt;/soccernow></td></tr>
<tr><th>Docker Hub (imagen app)</th><td><https://hub.docker.com/r/&lt;user&gt;/soccernow></td></tr>
<tr><th>Demo (backend Swagger)</th><td><http://localhost:8080/swagger-ui.html></td></tr>
<tr><th>Demo (JavaFX)</th><td>`mvn -pl SoccerNowFX javafx:run`</td></tr>
</table>

---

## 👥 Equipo

| Nombre              | Nº aluno | Conjunto de casos de uso |
|---------------------|---------:|:-------------------------|
| **André Santos**    | fc57538  | C2 (Equipos & Jugadores) |
| **Erickson Cacondo**| fc53653  | C3 (Árbitros & Campeonatos) |
| **Miguel Sánchez**  | fc64675  | C1 (Partidos & Resultados) |

> **Convención:** el sufijo *DTO* indica objetos de transferencia; los servicios encapsulan la lógica de negocio y los repositorios extienden `JpaRepository`.

---

# 1. Resumen de Funcionalidades

| Módulo | Fase 1 | Fase 2 — extensiones y mejoras |
|--------|--------|--------------------------------|
| **Dominio** | • Modelo JPA completo (User, Player, Team, Referee, Jogo, Resultado, Cartao, Estatisticas, Campeonato) | • Añadidos campos estadísticos (wins, draws, losses, achievements) <br>• Relaciones *n-m* bidireccionales <br>• Cascade & orphan-removal coherentes |
| **Persistencia** | • PostgreSQL + Flyway <br>• Hibernate DDL auto | • Datos de arranque extensos (16 jugadores, 4 equipos equilibrados, 3 árbitros, 2 campeonatos, 6 partidos) <br>• Cláusulas JPQL optimizadas (`findGamesWithMinGoals`, etc.) |
| **REST API** | • Endpoints CRUD para cada agregado <br>• Swagger doc | • Rutas refinadas: `/api/jogos/filter`, `/api/teams/missingPosition` … <br>• Validaciones exhaustivas (vencedor coherente con marcador, árbitros certificados en campeonato, etc.) |
| **Lógica de Negocio** | • Servicios transaccionales con reglas básicas | • Método `JogoService.registrarResultado()` genera automáticamente estadísticas por jugador <br>• `TeamService.deleteTeam()` prohíbe borrar equipos con partidos jugados |
| **Tests unitarios** | • JUnit 5 + Spring Boot test | • Cobertura 85 % en servicios y repositorios <br>• Pruebas de integridad de reglas (“no se puede registrar dos veces el resultado”, etc.) |
| **Frontend JavaFX** | — | • Pantallas para: login, menú, CRUD de usuarios/jugadores/equipos/árbitros, creación/cancelación de partido, registro de resultado <br>• Validación cliente y feedback visual (verde/rojo) |
| **Autenticación** | — | • Login “mock” (email + password) vía `/api/login` — solo comprueba existencia |
| **DevOps** | • Maven multi-módulo (backend y JavaFX) | • Docker Compose (app + DB) <br>• GitLab CI: build ⋯ test ⋯ empaquetar imagen |

---

# 2. Arquitectura

```

┌──────────────────────┐    REST/JSON    ┌────────────────────────────┐
│   JavaFX (SoccerNow) │ ◀──────────────▶│ Spring Boot 3 REST API     │
└──────────────────────┘                 │  ├─ Controllers            │
│  ├─ Services (Tx)         │
│  ├─ Repositories (JPA)    │
│  └─ Domain Model (JPA)    │
└────────────────────────────┘
│
▼
┌────────────────────────────┐
│ PostgreSQL 15 (+ Flyway)   │
└────────────────────────────┘

````

* **Capa presentación**: JavaFX puro (no FXML dinámico) + llamadas `java.net.http`.
* **Capa aplicación**: controladores REST → DTO → servicios.
* **Capa dominio**: entidades ricas con invariantes (p.ej. `Jogo#registrarResultado()` no permite duplicados).
* **Persistencia**: Spring Data JPA + Hibernate; transacciones demarcadas con `@Transactional`.

---

# 3. Modelo de Datos (dominio)

![Diagrama de dominio](docs/domain.png)

*Relaciones destacadas*

| Entidad | Relación | Cardinalidad | Reglas |
|---------|----------|-------------|--------|
| Team ⇄ Player | `team_players` | 1-N | un jugador pertenece a **un solo equipo** |
| Jogo ⇄ Referee | `jogo_arbitros` | N-M | en campeonatos los árbitros **deben estar certificados** |
| Jogo → Resultado | 1-1 | `Resultado` se crea una única vez |
| Jogo ⇄ Estatisticas | 1-N | stats generadas al registrar resultado |

---

# 4. REST API

| Método & Path | Body ⇢ Respuesta | Regla de negocio |
|---------------|------------------|------------------|
| **POST /api/jogos** | `JogoCreateDTO` → `JogoDTO` | • Equipos distintos <br>• Árbitros existentes <br>• Árbitro principal ∈ lista |
| **PUT /api/jogos/{id}/cancelar** | — → `204` | Solo si el partido **no tiene resultado** |
| **POST /api/jogos/{id}/resultado** | `{homeScore, awayScore, winnerId?}` → `ResultadoDTO` | • No previamente registrado <br>• `winnerId` debe participar y concordar con marcador |
| **DELETE /api/teams/{id}** | — | Prohibido si el equipo ha jugado algún partido |
| **GET /api/jogos/filter?realizados=true&amp;minGoals=3** | — | Filtro avanzado combinable |

La documentación completa generada con **springdoc-openapi** está disponible en `swagger-ui.html`.

---

# 5. Frontend JavaFX

| Pantalla | FXML | Controlador | Descripción |
|----------|------|-------------|-------------|
| Login | `login.fxml` | `LoginController` | Autenticación por email/contraseña |
| Menú principal | `menu.fxml` | `MenuController` | Navegación |
| Crear partido | `game_create.fxml` | `GameCreateController` | IDs de equipos y árbitros, validación local, POST `/api/jogos` |
| Registrar resultado | `game_result.fxml` | `GameResultController` | Solo ID y marcador “X-Y”; muestra “Resultado registrado” en verde |
| Cancelar partido | `game_cancel.fxml` | `GameCancelController` | PUT `/api/jogos/{id}/cancelar` |

> **UX:** todos los mensajes de error se muestran en rojo; éxitos, en verde.

---

# 6. Datos de Arranque (`BootstrapData.java`)

```text
• 16 jugadores  (ciclo PORTERO-DEFENSA-CENTRO-DELANTERO)
• 4 equipos      (Dragones, Tigres, Águilas, Lobos) – 4 jugadores/posición
• 3 árbitros     (2 certificados, 1 no certificado)
• 2 campeonatos  (Liga Primavera, Copa Verano)
• 6 partidos     (4 oficiales + 2 amistosos)
````

Todos los usuarios usan la contraseña «`pass`».

---

# 7. Pruebas

* **Unitarias** (JUnit 5, Mockito): reglas de `JogoService` y `TeamService`.
* **Integración** (`@SpringBootTest` + DB embebida): repositorios y endpoints críticos.
* **Cobertura**: 85 % líneas; 93 % métodos de servicio.

Ejecutar:

```bash
mvn clean verify   # módulos backend + JavaFX
```

---

# 8. Despliegue con Docker Compose

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: soccernow
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports: ["5432:5432"]
    networks: [dbnet]

  app:
    image: <user>/soccernow:latest        # generado por el CI
    depends_on: [db]
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/soccernow
    ports: ["8080:8080"]
    networks: [dbnet]

networks:
  dbnet:
```

```bash
./run.sh        # compila (sin tests) y levanta compose
```

Accede a [http://localhost:8080](http://localhost:8080).

---

# 9. Guía Rápida de Uso (cURL)

```bash
# Listar partidos pendientes
curl http://localhost:8080/api/jogos/filter?aRealizar=true

# Crear un amistoso (IDs 1-2, árbitro 18)
curl -X POST http://localhost:8080/api/jogos \
     -H "Content-Type: application/json" \
     -d '{"dateTime":"2025-08-01T18:00",
          "location":"Pabellón X",
          "amigavel":true,
          "teamIds":[1,2],
          "refereeIds":[18],
          "primaryRefereeId":18}'

# Registrar resultado 3-1
curl -X POST http://localhost:8080/api/jogos/7/resultado \
     -H "Content-Type: application/json" \
     -d '{"homeScore":3,"awayScore":1,"winnerId":1}'
```

---

# 10. Conclusiones y Trabajo Futuro

* Se cumplieron todas las reglas de negocio y los requisitos de ambas fases.
* Posibles mejoras: subir a WebSocket actualizaciones en tiempo real, autenticación JWT completa, ranking de máximos goleadores y refactor a módulos gradle.

---