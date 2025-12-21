package com.saf.restaurant;

import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.OrderStatus;
import com.saf.restaurant.model.StockResultPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestaurantStockKoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void should_reject_order_when_stock_missing() {
        OrderRequest order = new OrderRequest(
                "Carol",
                "T3",
                null,
                List.of(new OrderItem("unknown-item", 1))
        );

        ResponseEntity<OrderAcknowledgement> ackResponse = restTemplate.postForEntity(
                baseUrl + "/orders",
                order,
                OrderAcknowledgement.class
        );

        assertThat(ackResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        OrderAcknowledgement ack = ackResponse.getBody();
        assertThat(ack).isNotNull();
        assertThat(ack.status()).isEqualTo(OrderStatus.PENDING_STOCK);

        // Simule le callback stock KO
        StockResultPayload stockKo = new StockResultPayload(ack.orderId(), false, List.of("unknown-item"));
        ResponseEntity<Void> stockResp = restTemplate.postForEntity(
                baseUrl + "/events/stock-result",
                stockKo,
                Void.class
        );
        assertThat(stockResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // L'ack initial reste en mémoire uniquement; on vérifie que la commande est rejetée en récupérant les reçus (qui ne doit pas exister)
        ResponseEntity<String> receiptResp = restTemplate.getForEntity(
                baseUrl + "/receipts/" + ack.orderId(),
                String.class
        );
        assertThat(receiptResp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
