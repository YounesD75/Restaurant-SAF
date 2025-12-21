package com.saf.client;

import com.saf.client.dto.MenuItemDto;
import com.saf.client.dto.OrderAcknowledgementDto;
import com.saf.client.dto.OrderRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/client")
public class RestaurantProxyController {

    private final RestTemplate restTemplate;
    private final String restaurantBaseUrl;

    public RestaurantProxyController(RestTemplate restTemplate,
                                     @Value("${saf.restaurant.url:http://saf-restaurant}") String restaurantBaseUrl) {
        this.restTemplate = restTemplate;
        this.restaurantBaseUrl = restaurantBaseUrl;
    }

    @GetMapping("/menu")
    public List<MenuItemDto> listMenu() {
        ResponseEntity<List<MenuItemDto>> response = restTemplate.exchange(
                restaurantBaseUrl + "/menu",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @PostMapping("/orders")
    public OrderAcknowledgementDto placeOrder(@RequestBody OrderRequestDto request) {
        return restTemplate.postForObject(
                restaurantBaseUrl + "/orders",
                new HttpEntity<>(request),
                OrderAcknowledgementDto.class
        );
    }
}
