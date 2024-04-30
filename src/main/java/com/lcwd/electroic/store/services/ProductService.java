package com.lcwd.electroic.store.services;

import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.dtos.ProductDto;

import java.util.List;

public interface ProductService {
    //create
    ProductDto create(ProductDto productDto);
    //update
    ProductDto update(ProductDto productDto, String productId);
    //delete
    void delete(String productId);
    //get single
    ProductDto get(String productId);
    //get all
    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    //get all: Live
    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);
    //search product
    PageableResponse<ProductDto> searchByTitle(String subTitle, int pageNumber, int pageSize, String sortBy, String sortDir);
    //create product with category
    ProductDto createWithCategory(ProductDto productDto,String category);
    //update category of product
    ProductDto updateCategory(String productId,String categoryId);
    //return products of given  category
    PageableResponse<ProductDto> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir);

}
