package com.saf.client;

import com.saf.client.dto.MenuItemDto;
import com.saf.client.dto.OrderAcknowledgementDto;
import com.saf.client.dto.OrderItemDto;
import com.saf.client.dto.OrderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientProxyIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${saf.restaurant.url:http://localhost:8080}")
    private String restaurantUrl;

    @Test
    void should_relay_menu_and_order_to_restaurant() {
        String baseUrl = "http://localhost:" + port + "/client";

        ResponseEntity<MenuItemDto[]> menuResp = restTemplate.getForEntity(baseUrl + "/menu", MenuItemDto[].class);
        assertThat(menuResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(menuResp.getBody()).isNotNull();

        OrderRequestDto order = new OrderRequestDto(
                "Bob",
                "T2",
                "extra sauce",
                List.of(new OrderItemDto("burger", 1))
        );
        ResponseEntity<OrderAcknowledgementDto> ackResp = restTemplate.postForEntity(
                baseUrl + "/orders",
                order,
                OrderAcknowledgementDto.class
        );
        assertThat(ackResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderAcknowledgementDto ack = ackResp.getBody();
        assertThat(ack).isNotNull();
        assertThat(ack.status()).isEqualTo("PENDING_STOCK");
    }
}
