# ARSW - Laboratorio: Estrategia Integral de Pruebas

API de pedidos en Spring Boot usada como base para aplicar pruebas por capas.

## Requisitos
- Java 17+
- Maven 3.9+
- (Opcional) Docker, para Testcontainers
- (Opcional) k6, para pruebas de carga

## Abrir en IntelliJ
1. `File > Open...` y seleccione la carpeta `arsw-testing-lab` (la que contiene `pom.xml`).
2. IntelliJ detecta el proyecto Maven y descarga dependencias.
3. Verifique en `File > Project Structure > Project` que el SDK sea Java 17+.
4. Ejecute `TestingApplication` (boton verde) o `mvn spring-boot:run`.

## Endpoints
- `POST /orders` -> 201 Created. Body: `{ "customerId": "CUS-01", "total": 120000 }`
- `GET /orders/{id}` -> 200 OK
- Consola H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:ordersdb`)

## Regla de negocio
Un pedido con `total > 5.000.000` es rechazado.

## Pruebas
```bash
mvn test                # todas las pruebas
mvn test -Dtest=OrderServiceTest        # unitarias
mvn test -Dtest=OrderControllerTest     # API con MockMvc
mvn test -Dtest=OrderIntegrationTest    # integracion
```

| Clase | Tipo | Que valida |
|---|---|---|
| `OrderServiceTest` | Unitaria (JUnit + Mockito) | Reglas de negocio con el repositorio simulado |
| `OrderControllerTest` | API (`@WebMvcTest` + MockMvc) | Codigos HTTP, JSON, validaciones |
| `OrderIntegrationTest` | Integracion (`@SpringBootTest`) | Servicio + repositorio + H2 |
| `OrderPostgresIntegrationTest` | Integracion (Testcontainers) | Servicio + repositorio + PostgreSQL real (requiere Docker) |

## Carga con k6
```bash
mvn spring-boot:run          # en una terminal
k6 run load-tests/load-test.js   # en otra
```
