package com.lcwd.electroic.store.services.impl;

import com.lcwd.electroic.store.dtos.CategoryDto;
import com.lcwd.electroic.store.dtos.PageableResponse;
import com.lcwd.electroic.store.entities.Category;
import com.lcwd.electroic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electroic.store.helper.Helper;
import com.lcwd.electroic.store.repositories.CategoryRepository;
import com.lcwd.electroic.store.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper mapper;


    @Value("${category.profile.image.path}")
    private String imagePath;
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        //creating ramdom Ids
        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);
       Category category =  mapper.map(categoryDto, Category.class);
       Category savedCategory = categoryRepository.save(category);
       return mapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
       Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("Category Not found exception"));
       category.setTitle(categoryDto.getTitle());
       category.setDescription(categoryDto.getDescription());
       category.setCoverImage(categoryDto.getCoverImage());
       Category updatedCategory = categoryRepository.save(category);
        return mapper.map(updatedCategory, CategoryDto.class);

    }

    @Override
    public void delete(String categoryId) throws IOException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()-> new ResourceNotFoundException("Category not found exception"));
        String fullPath = imagePath+ category.getCoverImage();
        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }catch(NoSuchFileException e){
            e.printStackTrace();
        }
        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize,String sortBy, String sortDir ) {

        Sort sort = (sortDir.equalsIgnoreCase("desc"))?(Sort.by(sortBy).descending()):(Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        PageableResponse<CategoryDto> response= Helper.getPageableResponse(page,CategoryDto.class);
        return response;
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category not found by id"));

        return mapper.map(category,CategoryDto.class);
    }
}
