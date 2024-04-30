package com.lcwd.electroic.store.controllers;

import com.lcwd.electroic.store.dtos.*;
import com.lcwd.electroic.store.services.CategoryService;
import com.lcwd.electroic.store.services.FileService;
import com.lcwd.electroic.store.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;
    @Value("${product.profile.image.path}")
    private String imageUploadPath;

    @PreAuthorize("hasRole('ADMIN')")
    //create
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto categoryDto1 = categoryService.create(categoryDto);
        return new ResponseEntity<>(categoryDto1, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    //update
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String categoryId,@Valid @RequestBody CategoryDto categoryDto){
        CategoryDto categoryDto1 = categoryService.update(categoryDto,categoryId);
        return new ResponseEntity<>(categoryDto1, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    //delete
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId) throws IOException {
        categoryService.delete(categoryId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("Category is deleted successfully").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
    //get all

    @GetMapping()
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                                 @RequestParam(value ="sortBy",defaultValue = "title",required = false)String sortBy,
                                                                 @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<CategoryDto> categoryDtoList = categoryService.getAll(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(categoryDtoList, HttpStatus.OK);
    }
    //get single
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String categoryId){
        CategoryDto categoryDto = categoryService.get(categoryId);
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    //upload category image
    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCoverImage(@RequestParam("categoryImage") MultipartFile image,
                                                         @PathVariable String categoryId) throws IOException {
        String imageName = fileService.uploadFile(image,imageUploadPath);
        CategoryDto category = categoryService.get(categoryId);
        category.setCoverImage(imageName);
        CategoryDto categoryDto = categoryService.update(category, categoryId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")

    //serve u serimage
    @GetMapping("/image/{categoryId}")
    public void serveCoverImage(@PathVariable String categoryId, HttpServletResponse response) throws IOException {
        CategoryDto category = categoryService.get(categoryId);
        InputStream resource = fileService.getResource(imageUploadPath,category.getCoverImage());
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    //create product with category
    @PostMapping("{categoryId}/products")
    ResponseEntity<ProductDto> createProductWithCategory(@PathVariable("categoryId")String categoryId,@RequestBody ProductDto productDto){
            ProductDto productWithCategory = productService.createWithCategory(productDto,categoryId);
            return new ResponseEntity<>(productWithCategory,HttpStatus.CREATED);

    }

    //update category of product
    @PutMapping("/{categoryId}/products/{productId}")
    ResponseEntity<ProductDto> updateCategoryOfProduct(@PathVariable String categoryId,@PathVariable String productId){
        ProductDto productDto = productService.updateCategory(productId,categoryId);
        return new ResponseEntity<>(productDto,HttpStatus.OK);

    }
    //get products of categories
    @GetMapping("/{categoryId}/products")
    ResponseEntity<PageableResponse<ProductDto>> getProductsOfCategory(@PathVariable String categoryId, @RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                                       @RequestParam(value ="sortBy",defaultValue = "title",required = false)String sortBy,
                                                                       @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<ProductDto> response = productService.getAllOfCategory(categoryId, pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

}
