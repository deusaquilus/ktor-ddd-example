package domain.reminder

import com.example.domain.customer.CustomerId
import com.example.domain.customer.NoteId
import java.time.LocalDateTime
import java.util.*

data class ReminderId(val value: String)

data class Reminder(
    val id: ReminderId = ReminderId(UUID.randomUUID().toString()),
    val customerId: CustomerId, // always links to a customer
    val noteId: NoteId?, // Optionally linked to a note
    val remindAt: LocalDateTime,
    val message: String
)