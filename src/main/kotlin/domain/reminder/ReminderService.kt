package com.example.domain.reminder

import com.example.domain.customer.CustomerId
import com.example.domain.customer.NoteId
import domain.reminder.Reminder
import domain.reminder.ReminderId
import domain.reminder.ReminderRepository
import java.time.LocalDateTime

// ReminderService encapsulates the business logic for creating and retrieving reminders.
class ReminderService(private val reminderRepository: ReminderRepository) {

    suspend fun createReminder(customerId: CustomerId, noteId: Int?, remindAt: LocalDateTime, message: String): Reminder {
        val reminder = Reminder(
            customerId = customerId,
            noteId = noteId?.let { NoteId(it) },
            remindAt = remindAt,
            message = message
        )
        return reminderRepository.save(reminder)
    }

    suspend fun getReminder(id: Long): Reminder? {
        return reminderRepository.findById(ReminderId(id))
    }

    suspend fun getRemindersForCustomer(customerId: CustomerId): List<Reminder> {
        return reminderRepository.findByContact(customerId)
    }
}
