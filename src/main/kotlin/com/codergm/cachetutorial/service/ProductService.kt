package com.codergm.cachetutorial.service

import com.codergm.cachetutorial.dto.ProductDto
import com.codergm.cachetutorial.repository.ProductRepository
import com.codergm.cachetutorial.mapper.toDto
import com.codergm.cachetutorial.mapper.toProduct
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    @CachePut(value = ["PRODUCT_CACHE"], key = "#result.id")
    fun createProduct(productDto: ProductDto): ProductDto = productRepository.save(productDto.toProduct()).toDto()

    @Cacheable(value = ["PRODUCT_CACHE"], key = "#result.id")
    fun getProductById(id: Long): ProductDto =
        productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Cannot find product with id $id") }
            .toDto()

    @CachePut(value = ["PRODUCT_CACHE"], key = "#result.id")
    fun updateProduct(id: Long, productDto: ProductDto): ProductDto {
        productRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Cannot find product with id $id") }
        return productRepository.save(productDto.toProduct(id)).toDto()
    }

    @CacheEvict(value = ["PRODUCT_CACHE"], key = "#result.id")
    fun deleteProduct(id: Long): Unit = productRepository.deleteById(id)
}