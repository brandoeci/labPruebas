package edu.eci.arsw.testing.service;

import edu.eci.arsw.testing.dto.CreateOrderRequest;
import edu.eci.arsw.testing.dto.OrderResponse;
import edu.eci.arsw.testing.model.Order;
import edu.eci.arsw.testing.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository repository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrderRepository.class);
        service = new OrderService(repository);
    }

    @Test
    @DisplayName("Crea el pedido cuando la solicitud es valida")
    void shouldCreateOrderWhenRequestIsValid() {
        Order savedOrder = new Order("ORD-1", "CUS-01", 120000, "CREATED", Instant.now());
        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        CreateOrderRequest request = new CreateOrderRequest("CUS-01", 120000);
        OrderResponse response = service.createOrder(request);

        assertNotNull(response);
        assertEquals("ORD-1", response.id());
        assertEquals("CUS-01", response.customerId());
        assertEquals(120000, response.total());
        assertEquals("CREATED", response.status());

        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Rechaza el pedido cuando el total supera el limite de negocio")
    void shouldRejectOrderWhenTotalExceedsLimit() {
        CreateOrderRequest request = new CreateOrderRequest("CUS-01", 6000000);

        assertThrows(IllegalArgumentException.class, () -> service.createOrder(request));
        verify(repository, never()).save(any(Order.class));
    }

    // ---- Actividad 1 ----

    @Test
    @DisplayName("findById retorna el pedido cuando existe")
    void shouldReturnOrderWhenItExists() {
        Order existing = new Order("ORD-1", "CUS-01", 120000, "CREATED", Instant.now());
        when(repository.findById("ORD-1")).thenReturn(Optional.of(existing));

        OrderResponse response = service.findById("ORD-1");

        assertEquals("ORD-1", response.id());
        assertEquals("CUS-01", response.customerId());
        assertEquals(120000, response.total());
        assertEquals("CREATED", response.status());
        verify(repository, times(1)).findById("ORD-1");
    }

    @Test
    @DisplayName("findById lanza excepcion cuando el pedido no existe")
    void shouldThrowWhenOrderDoesNotExist() {
        when(repository.findById("ORD-404")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.findById("ORD-404")
        );
        assertEquals("Pedido no encontrado", ex.getMessage());
    }
}
