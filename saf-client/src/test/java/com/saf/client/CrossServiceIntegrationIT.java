package com.saf.client;

import com.saf.client.dto.MenuItemDto;
import com.saf.client.dto.OrderAcknowledgementDto;
import com.saf.client.dto.OrderItemDto;
import com.saf.client.dto.OrderRequestDto;
import com.saf.client.dto.ReceiptDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CrossServiceIntegrationIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres");

    private ConfigurableApplicationContext inventory;
    private ConfigurableApplicationContext restaurant;
    private ConfigurableApplicationContext client;

    private int clientPort;
    private int inventoryPort;
    private int restaurantPort;

    @BeforeAll
    void startServices() throws Exception {
        createDatabase("saf_restaurant");
        createDatabase("inventory_db");

        inventoryPort = freePort();
        restaurantPort = freePort();
        clientPort = freePort();

        inventory = new SpringApplicationBuilder(appClass("com.InventoryService.InventoryService.InventoryServiceApplication"))
                .run(toArgs(withRestaurantCallback(commonProps("inventory-service", "inventory_db"), inventoryPort, restaurantPort)));

        restaurant = new SpringApplicationBuilder(appClass("com.saf.restaurant.SafRestaurantApplication"))
                .run(toArgs(withInventoryProps(commonProps("saf-restaurant", "saf_restaurant"), inventoryPort, restaurantPort)));

        client = new SpringApplicationBuilder(SafClientApplication.class)
                .run(toArgs(disableDiscoveryProps(Map.of(
                        "server.port", Integer.toString(clientPort),
                        "saf.restaurant.url", "http://localhost:" + restaurantPort
                ))));

        Thread.sleep(2000);
    }

    @AfterAll
    void stopServices() {
        if (client != null) {
            client.close();
        }
        if (restaurant != null) {
            restaurant.close();
        }
        if (inventory != null) {
            inventory.close();
        }
    }

    @Test
    void fullFlow_menu_order_receipt() {
        RestTemplate rest = new RestTemplate();
        String baseUrl = "http://localhost:" + clientPort + "/client";

        ResponseEntity<MenuItemDto[]> menuResponse =
                rest.getForEntity(baseUrl + "/menu", MenuItemDto[].class);
        assertThat(menuResponse.getBody()).isNotNull();
        assertThat(menuResponse.getBody().length).isGreaterThan(0);

        OrderRequestDto order = new OrderRequestDto(
                "Alice",
                "12",
                "sans oignons",
                List.of(
                        new OrderItemDto("burger", 2),
                        new OrderItemDto("drink", 2)
                )
        );

        OrderAcknowledgementDto ack =
                rest.postForObject(baseUrl + "/orders", order, OrderAcknowledgementDto.class);
        assertThat(ack).isNotNull();
        assertThat(ack.orderId()).isNotNull();

        ReceiptDto receipt = waitForReceipt(rest, baseUrl + "/receipts/" + ack.orderId(), Duration.ofSeconds(10));
        assertThat(receipt).isNotNull();
        assertThat(receipt.items()).isNotEmpty();
        assertThat(receipt.total()).isNotNull();
    }

    private ReceiptDto waitForReceipt(RestTemplate rest, String url, Duration timeout) {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < deadline) {
            try {
                ResponseEntity<ReceiptDto> response = rest.getForEntity(url, ReceiptDto.class);
                if (response.getBody() != null) {
                    return response.getBody();
                }
            } catch (RestClientException ignored) {
                // Receipt pas encore prêt
            }
            sleep(500);
        }
        return null;
    }

    private Map<String, Object> commonProps(String appName, String dbName) {
        Map<String, Object> props = new HashMap<>();
        props.put("spring.application.name", appName);
        props.put("spring.datasource.url", jdbcUrl(dbName));
        props.put("spring.datasource.username", POSTGRES.getUsername());
        props.put("spring.datasource.password", POSTGRES.getPassword());
        props.put("spring.jpa.show-sql", "false");
        // Override test-classpath exclusions from saf-client test resources.
        props.put("spring.autoconfigure.exclude", "");
        return disableDiscoveryProps(props);
    }

    private String jdbcUrl(String dbName) {
        return "jdbc:postgresql://" + POSTGRES.getHost() + ":" + POSTGRES.getMappedPort(5432) + "/" + dbName;
    }

    private void createDatabase(String name) throws Exception {
        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE " + name);
        } catch (Exception e) {
            if (e instanceof java.sql.SQLException sqlEx) {
                // 42P04 = duplicate_database
                if ("42P04".equals(sqlEx.getSQLState())) {
                    return;
                }
            }
            throw e;
        }
    }

    private Class<?> appClass(String fqcn) throws ClassNotFoundException {
        return Class.forName(fqcn);
    }

    private Map<String, Object> withInventoryProps(Map<String, Object> props, int inventoryPort, int serverPort) {
        Map<String, Object> merged = new HashMap<>(props);
        merged.put("server.port", Integer.toString(serverPort));
        merged.put("saf.inventory.url", "http://localhost:" + inventoryPort);
        merged.put("saf.inventory.check-path", "/api/inventory/check");
        merged.put("saf.inventory.enabled", "true");
        return merged;
    }

    private Map<String, Object> withRestaurantCallback(Map<String, Object> props, int serverPort, int restaurantPort) {
        Map<String, Object> merged = new HashMap<>(props);
        merged.put("server.port", Integer.toString(serverPort));
        merged.put("saf.restaurant.url", "http://localhost:" + restaurantPort);
        return merged;
    }

    private Map<String, Object> disableDiscoveryProps(Map<String, Object> props) {
        Map<String, Object> merged = new HashMap<>(props);
        merged.put("eureka.client.enabled", "false");
        merged.put("spring.cloud.discovery.enabled", "false");
        merged.put("spring.cloud.loadbalancer.enabled", "false");
        return merged;
    }

    private String[] toArgs(Map<String, Object> props) {
        return props.entrySet().stream()
                .map(entry -> "--" + entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
    }

    private int freePort() throws Exception {
        try (java.net.ServerSocket socket = new java.net.ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
