package dev.elelan.quote_quiz_server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class TestResponse(
    val status: String,
    val service: String,
)

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping
    fun getTestResponse(): TestResponse =
        TestResponse(
            status = "ok",
            service = "quote-quiz-server",
        )
}
