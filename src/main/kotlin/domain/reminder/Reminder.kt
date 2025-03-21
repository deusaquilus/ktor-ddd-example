package domain.reminder

import com.example.domain.customer.CustomerId
import com.example.domain.customer.NoteId
import kotlinx.serialization.Serializable
import serialization.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class ReminderId(val value: Long)

@Serializable
data class Reminder(
    val id: ReminderId? = null,
    val customerId: CustomerId, // always links to a customer
    val noteId: NoteId?, // Optionally linked to a note
    @Serializable(with = LocalDateTimeSerializer::class)
    val remindAt: LocalDateTime,
    val message: String
)
