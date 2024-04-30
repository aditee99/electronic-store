package com.lcwd.electroic.store.services;

import com.lcwd.electroic.store.dtos.CategoryDto;
import com.lcwd.electroic.store.dtos.PageableResponse;

import java.io.IOException;

public interface CategoryService {
    //create
    CategoryDto create(CategoryDto categoryDto);
    //update
    CategoryDto update(CategoryDto categoryDto, String categoryId);
    //delete
    void delete(String categoryId) throws IOException;
    //get all
    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);
    //get
    CategoryDto get(String categoryId);

}
