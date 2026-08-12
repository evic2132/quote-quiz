package dev.elelan.quote_quiz_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuoteQuizServerApplication

fun main(args: Array<String>) {
    runApplication<QuoteQuizServerApplication>(*args)
}
