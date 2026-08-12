package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table

@Entity
@Table(name = "quiz_sessions")
class QuizSessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val sessionId: String,
    @Column(nullable = false)
    val userId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val mode: QuizMode,
    @Column(nullable = false)
    var correctAnswers: Int = 0,
    @Column(nullable = false)
    var completed: Boolean = false,
    @OneToMany(
        mappedBy = "session",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.EAGER,
    )
    @OrderBy("progress ASC")
    val questions: MutableList<QuizSessionQuestionEntity> = mutableListOf(),
)
