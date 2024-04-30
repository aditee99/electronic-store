package com.lcwd.electroic.store.dtos;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddItemToCardRequest {
    private String productId;
    private int quantity;
}
