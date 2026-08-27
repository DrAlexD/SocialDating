package xelagurd.socialdating.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CategoriesServiceApplication

fun main(args: Array<String>) {
    runApplication<CategoriesServiceApplication>(*args)
}