package dev.elelan.quote_quiz_server.quiz

import dev.elelan.quotequiz.contract.quiz.QuizMode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "quiz_session_questions")
class QuizSessionQuestionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val questionId: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    val session: QuizSessionEntity,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val mode: QuizMode,
    @Column(nullable = false, length = 1000)
    val quoteText: String,
    @Column(nullable = false)
    val correctAuthor: String,
    @Column(nullable = false)
    val progress: Int,
    @Column
    val proposedAuthor: String? = null,
    @Column
    val optionOne: String? = null,
    @Column
    val optionTwo: String? = null,
    @Column
    val optionThree: String? = null,
    @Column(nullable = false)
    var answered: Boolean = false,
    @Column
    var wasCorrect: Boolean? = null,
) {
    @get:Transient
    val options: List<String>
        get() = listOfNotNull(optionOne, optionTwo, optionThree)
}
