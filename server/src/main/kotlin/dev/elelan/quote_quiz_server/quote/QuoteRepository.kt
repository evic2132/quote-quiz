package dev.elelan.quote_quiz_server.quote

import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRepository : JpaRepository<QuoteEntity, Long>
