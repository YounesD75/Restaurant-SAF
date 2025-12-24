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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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

    @Autowired
    private RestTemplate inventoryRestTemplate;

    private MockRestServiceServer mockServer;
    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
        mockServer = MockRestServiceServer.createServer(inventoryRestTemplate);
    }

    @Test
    void should_call_inventory_after_order_is_placed() {
        mockServer.expect(requestTo("http://localhost/api/inventory/check"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

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
        mockServer.verify();
    }

    @TestConfiguration
    static class TestRestTemplateConfig {
        @Bean
        @Primary
        RestTemplate plainRestTemplate() {
            return new RestTemplate();
        }
    }
}
