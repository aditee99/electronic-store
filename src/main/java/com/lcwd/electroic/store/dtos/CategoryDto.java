package com.lcwd.electroic.store.dtos;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private String categoryId;
    @NotBlank(message = "title is required!!")
    @Size(min= 4,message = "title must be of minimum 4 characters")
    private String title;
    @NotBlank(message = "Description required")
    private String description;
    @NotBlank(message = "Cover Image required")
    private String coverImage;
}
