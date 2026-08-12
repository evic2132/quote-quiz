package dev.elelan.quote_quiz_server.api

import dev.elelan.quotequiz.contract.quiz.QuizMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JacksonModule
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.module.SimpleModule

@Configuration
class JacksonConfig {

    @Bean
    fun quizModeModule(): JacksonModule =
        SimpleModule()
            .addSerializer(QuizMode::class.java, QuizModeSerializer())
            .addDeserializer(QuizMode::class.java, QuizModeDeserializer())
}

private class QuizModeSerializer : ValueSerializer<QuizMode>() {
    override fun serialize(value: QuizMode, gen: JsonGenerator, ctxt: SerializationContext) {
        val wireValue =
            when (value) {
                QuizMode.BINARY -> "binary"
                QuizMode.MULTIPLE_CHOICE -> "multiple_choice"
            }
        gen.writeString(wireValue)
    }
}

private class QuizModeDeserializer : ValueDeserializer<QuizMode>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): QuizMode =
        when (p.valueAsString) {
            "binary" -> QuizMode.BINARY
            "multiple_choice" -> QuizMode.MULTIPLE_CHOICE
            else -> ctxt.handleWeirdStringValue(QuizMode::class.java, p.valueAsString, "Unsupported quiz mode") as QuizMode
        }
}
