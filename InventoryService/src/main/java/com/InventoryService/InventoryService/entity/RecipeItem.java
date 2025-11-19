package com.InventoryService.InventoryService.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipe_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private Ingredient ingredient;

    private Integer quantityNeeded;
}
