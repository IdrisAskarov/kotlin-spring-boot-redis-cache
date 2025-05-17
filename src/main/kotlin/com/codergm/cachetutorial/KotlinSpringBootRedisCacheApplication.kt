package com.codergm.cachetutorial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringBootRedisCacheApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringBootRedisCacheApplication>(*args)
}
