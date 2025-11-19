package com.InventoryService.InventoryService.agent;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {
    private String type;    // e.g. "RESERVE", "RELEASE", "CHECK"
    private Object payload; // ReserveRequest, etc
}

