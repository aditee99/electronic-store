package com.lcwd.electroic.store.controllers;

import com.lcwd.electroic.store.dtos.*;
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
@RequestMapping("/products")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    FileService fileService;
    @Value("${product.profile.image.path}")
    private String imageUploadPath;
    //create
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto){
        ProductDto productDto1 = productService.create(productDto);
        return new ResponseEntity<>(productDto1, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ADMIN')")
    //update
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String productId,@Valid @RequestBody ProductDto productDto){
        ProductDto productDto1 = productService.update(productDto,productId);
        return new ResponseEntity<>(productDto1, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ADMIN')")
    //delete
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId) throws IOException {
        productService.delete(productId);
        ApiResponseMessage message = ApiResponseMessage.builder().message("Product is deleted successfully").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }
    //get all

    @GetMapping()
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                                          @RequestParam(value ="sortBy",defaultValue = "title",required = false)String sortBy,
                                                                          @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<ProductDto> productDtoList = productService.getAll(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }
    //get single
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId){
        ProductDto productDto = productService.get(productId);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                                       @RequestParam(value ="sortBy",defaultValue = "title",required = false)String sortBy,
                                                                       @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<ProductDto> productDtoList = productService.getAllLive(pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProductsBySubtitle(@PathVariable String query,@RequestParam(value ="pageNumber",defaultValue = "0",required = false) int pageNumber, @RequestParam(value="pageSize",defaultValue = "0", required = false) int pageSize,
                                                                           @RequestParam(value ="sortBy",defaultValue = "title",required = false)String sortBy,
                                                                           @RequestParam(value ="sortDir",defaultValue = "asc",required = false)String sortDir){
        PageableResponse<ProductDto> productDtoList = productService.searchByTitle(query, pageNumber,pageSize,sortBy,sortDir);
        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

    //upload product image
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(@RequestParam("productImage") MultipartFile image,
                                                          @PathVariable String productId) throws IOException {
        String imageName = fileService.uploadFile(image,imageUploadPath);
        ProductDto product = productService.get(productId);
        product.setProductImage(imageName);
        ProductDto productDto = productService.update(product, productId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }


    //serve u serve image
    @GetMapping("/image/{productId}")
    public void serveProductImage(@PathVariable String productId, HttpServletResponse response) throws IOException {
        ProductDto product = productService.get(productId);
        InputStream resource = fileService.getResource(imageUploadPath,product.getProductImage());
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }


}
