package edu.eci.arsw.testing.integration;

import edu.eci.arsw.testing.dto.CreateOrderRequest;
import edu.eci.arsw.testing.dto.OrderResponse;
import edu.eci.arsw.testing.service.OrderService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Seccion 7.1 - Prueba de integracion contra una base de datos PostgreSQL real
 * levantada en Docker por Testcontainers.
 *
 * Requiere Docker corriendo. Quite el @Disabled cuando tenga Docker Desktop activo.
 */
@Disabled("Requiere Docker en ejecucion")
@Testcontainers
@SpringBootTest
class OrderPostgresIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private OrderService service;

    @Test
    void shouldPersistOrderInRealDatabase() {
        OrderResponse created = service.createOrder(new CreateOrderRequest("CUS-77", 300000));
        OrderResponse found = service.findById(created.id());

        assertEquals(created.id(), found.id());
        assertEquals("CUS-77", found.customerId());
    }
}
