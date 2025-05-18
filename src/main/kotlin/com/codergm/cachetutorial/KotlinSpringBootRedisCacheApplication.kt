package com.codergm.cachetutorial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class KotlinSpringBootRedisCacheApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBootRedisCacheApplication>(*args)
}
