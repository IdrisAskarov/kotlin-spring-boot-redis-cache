package com.codergm.cachetutorial

import com.codergm.cachetutorial.dto.ProductDto
import com.codergm.cachetutorial.entity.Product
import com.codergm.cachetutorial.repository.ProductRepository
import com.codergm.cachetutorial.service.ProductService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
class KotlinSpringBootRedisCacheApplicationTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val productRepository: ProductRepository,
    @Autowired val cacheManager: CacheManager

) {
    @MockitoSpyBean lateinit var productRepositorySpy: ProductRepository

    companion object {
        @Container
        @ServiceConnection
        val redis: GenericContainer<Nothing> = GenericContainer<Nothing>("redis:7.4.2").withExposedPorts(6379)
    }

    val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(kotlinModule())

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()
    }

    @Test
    fun `test create and cache product`() {
        val productDto = ProductDto(name = "Laptop", price = BigDecimal(1200))

        // Step 1. Create a Product
        val result: MvcResult = mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto))
        )
            .andExpect(status().isCreated())
            .andReturn()
        val createdProduct = objectMapper.readValue(
            result.response.contentAsString,
            ProductDto::class.java
        )
        val productId: Long = createdProduct.id!!


        // Step 2: Check Product Exists in DB
        assertThat(productRepository.findById(productId!!).isPresent)


        // Step 3: Check Cache
        val cache: Cache? = cacheManager.getCache(ProductService.PRODUCT_CACHE)
        assertThat(cache).isNotNull
        assertThat(cache?.get(productId, ProductDto::class.java) ?: null).isNotNull
    }

    @Test
    @Throws(Exception::class)
    fun `test get product and verify cache`() {
        // Step 1: Save product in DB
        val product = productRepository.save(Product(name = "Phone", price = BigDecimal.valueOf(800L)))

        // Step 2: Fetch product
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/" + product.id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Phone"))

        Mockito.verify(productRepositorySpy, Mockito.times(1)).findById(product.id!!)

        Mockito.clearInvocations(productRepositorySpy)

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/" + product.id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Phone"))

        Mockito.verify(productRepositorySpy, Mockito.times(0)).findById(product.id!!)
    }

    @Test
    @Throws(Exception::class)
    fun `test update product and verify cache`() {
        // Step 1: Create and Save Product
        val product = productRepository.save(Product(name = "Tablet", price = BigDecimal.valueOf(800L)))

        val updatedProductDto = ProductDto(product.id, "Updated Tablet", BigDecimal.valueOf(550L))

        // Step 2: Update Product
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/products/${product.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProductDto))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Tablet"))
            .andExpect(jsonPath("$.price").value(550.0))

        // Step 3: Verify Cache is Updated
        val cache: Cache? = cacheManager.getCache(ProductService.PRODUCT_CACHE)
        assertThat(cache).isNotNull
        val cachedProduct: ProductDto? = cache?.get(product.id!!, ProductDto::class.java)
        assertThat(cachedProduct).isNotNull
        assertThat("Updated Tablet").isEqualTo(cachedProduct?.name)
    }

    @Test
    @Throws(Exception::class)
    fun `test delete product and evict cache`() {
        // Step 1: Create and Save Product
        val product = productRepository.save(Product(name = "Smartwatch", price = BigDecimal.valueOf(250L)));

        // Step 2: Delete Product
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/" + product.id))
            .andExpect(status().isNoContent())

        // Step 3: Check that Product is Deleted from DB
        assertThat(productRepository.findById(product.id!!).isPresent).isFalse()

        // Step 4: Check Cache Eviction
        val cache: Cache? = cacheManager.getCache(ProductService.PRODUCT_CACHE)
        assertThat(cache).isNotNull
        assertThat(cache?.get(product.id!!)).isNull()
    }
}
