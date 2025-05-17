package com.codergm.cachetutorial.repository

import com.codergm.cachetutorial.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<Product, Long>