package com.saf.restaurant;

import com.saf.restaurant.model.OrderAcknowledgement;
import com.saf.restaurant.model.OrderItem;
import com.saf.restaurant.model.OrderRequest;
import com.saf.restaurant.model.OrderStatus;
import com.saf.restaurant.model.ReceiptDocument;
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
class RestaurantFlowIntegrationTest {

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
    void should_process_order_and_generate_receipt() throws Exception {
        OrderRequest order = new OrderRequest(
                "Alice",
                "T1",
                "no onions",
                List.of(new OrderItem("burger", 1))
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

        // Simule le callback stock OK
        StockResultPayload stockOk = new StockResultPayload(ack.orderId(), true, List.of());
        ResponseEntity<Void> stockResp = restTemplate.postForEntity(
                baseUrl + "/events/stock-result",
                stockOk,
                Void.class
        );
        assertThat(stockResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        // Attendre la génération du reçu
        ReceiptDocument receipt = waitForReceipt(ack.orderId());
        assertThat(receipt).isNotNull();
        assertThat(receipt.orderId()).isEqualTo(ack.orderId());
        assertThat(receipt.total()).isNotNull();
        assertThat(receipt.items()).isNotEmpty();
    }

    private ReceiptDocument waitForReceipt(Long orderId) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            ResponseEntity<ReceiptDocument> resp = restTemplate.getForEntity(
                    baseUrl + "/receipts/" + orderId,
                    ReceiptDocument.class
            );
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            }
            Thread.sleep(200);
        }
        return null;
    }
}
