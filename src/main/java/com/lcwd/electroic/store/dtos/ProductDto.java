package com.lcwd.electroic.store.dtos;
import com.lcwd.electroic.store.entities.Category;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private String productId;
    private String title;
    private String description;
    private double price;
    private double discountedPrice;
    private int quantity;
    private Date addedDate;
    private boolean live;
    private boolean stock;
    @NotBlank(message = "Cover Image required")
    private String productImage;
    private CategoryDto category;
}
