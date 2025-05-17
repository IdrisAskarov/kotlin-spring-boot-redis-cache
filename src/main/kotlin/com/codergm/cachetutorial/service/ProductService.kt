package com.codergm.cachetutorial.service

import com.codergm.cachetutorial.dto.ProductDto
import com.codergm.cachetutorial.repository.ProductRepository
import com.codergm.cachetutorial.mapper.toDto
import com.codergm.cachetutorial.mapper.toProduct
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun createProduct(productDto: ProductDto): ProductDto = productRepository.save(productDto.toProduct()).toDto()

    fun getProductById(id: Long): ProductDto =
        productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Cannot find product with id $id") }
            .toDto()

    fun updateProduct(id: Long, productDto: ProductDto): ProductDto {
        productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Cannot find product with id $id") }
        return productRepository.save(productDto.toProduct(id)).toDto()
    }

    fun deleteProduct(id: Long): Unit = productRepository.deleteById(id)
}