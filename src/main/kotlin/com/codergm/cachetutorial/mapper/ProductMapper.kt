package com.codergm.cachetutorial.mapper

import com.codergm.cachetutorial.dto.ProductDto
import com.codergm.cachetutorial.entity.Product

fun ProductDto.toProduct(): Product = Product(id = this.id, name = this.name, price = this.price)
fun ProductDto.toProduct(id: Long): Product = Product(id = id, name = this.name, price = this.price)

fun Product.toDto(): ProductDto = ProductDto(id = this.id, name = this.name, price = this.price)