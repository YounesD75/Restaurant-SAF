package com.saf.restaurant;

import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;
import com.saf.restaurant.client.InventoryCheckRequest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "saf.inventory.enabled=true",
                "saf.inventory.url=http://localhost",
                "saf.inventory.check-path=/api/inventory/check"
        }
)
class RestaurantInventoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RestTemplate inventoryRestTemplate;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void should_call_inventory_after_order_is_placed() {
        when(inventoryRestTemplate.postForEntity(
                eq("http://localhost/api/inventory/check"),
                any(InventoryCheckRequest.class),
                eq(Void.class))
        ).thenReturn(ResponseEntity.ok().build());

        OrderRequest order = new OrderRequest(
                "Alice",
                "T1",
                "no onions",
                List.of(new OrderItem("burger", 1))
        );

        OrderAcknowledgement ack = restTemplate.postForObject(
                baseUrl + "/orders",
                order,
                OrderAcknowledgement.class
        );

        assertThat(ack).isNotNull();
        assertThat(ack.status()).isEqualTo(OrderStatus.PENDING_STOCK);

        ArgumentCaptor<InventoryCheckRequest> payloadCaptor =
                ArgumentCaptor.forClass(InventoryCheckRequest.class);
        verify(inventoryRestTemplate).postForEntity(
                eq("http://localhost/api/inventory/check"),
                payloadCaptor.capture(),
                eq(Void.class)
        );
        InventoryCheckRequest payload = payloadCaptor.getValue();
        assertThat(payload).isNotNull();
        assertThat(payload.items()).isNotEmpty();
    }
}
