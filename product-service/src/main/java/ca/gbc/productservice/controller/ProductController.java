package ca.gbc.productservice.controller;

import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody ProductRequest productRequest) {
        String updatedId = productService.updateProduct(id, productRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", "/api/product/" + updatedId);
        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", "/api/product/" + id);
        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }

}
