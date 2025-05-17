package com.codergm.cachetutorial.controller

import com.codergm.cachetutorial.dto.ProductDto
import com.codergm.cachetutorial.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: ProductService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody @Valid productDto: ProductDto): ProductDto =
        productService.createProduct(productDto)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getProduct(@PathVariable id: Long): ProductDto = productService.getProductById(id)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun updateProduct(@PathVariable id: Long, @RequestBody productDto: ProductDto): ProductDto =
        productService.updateProduct(id, productDto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Long) = productService.deleteProduct(id)
}