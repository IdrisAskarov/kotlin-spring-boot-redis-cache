package com.codergm.cachetutorial.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class ProductDto(var id: Long? = null, @NotBlank val name: String, @Positive val price: BigDecimal)