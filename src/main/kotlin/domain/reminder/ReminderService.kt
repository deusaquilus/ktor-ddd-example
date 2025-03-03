package com.example.domain.reminder

import com.example.domain.customer.CustomerId
import com.example.domain.customer.NoteId
import domain.reminder.Reminder
import domain.reminder.ReminderId
import domain.reminder.ReminderRepository
import java.time.LocalDateTime
import java.util.UUID

// ReminderService encapsulates the business logic for creating and retrieving reminders.
class ReminderService(private val reminderRepository: ReminderRepository) {

    fun createReminder(customerId: CustomerId, noteId: String?, remindAt: LocalDateTime, message: String): Reminder {
        val reminder = Reminder(
            id = ReminderId(UUID.randomUUID().toString()),
            customerId = customerId,
            noteId = noteId?.let { NoteId(it) },
            remindAt = remindAt,
            message = message
        )
        reminderRepository.save(reminder)
        return reminder
    }

    fun getReminder(id: String): Reminder? {
        return reminderRepository.findById(ReminderId(id))
    }

    fun getRemindersForCustomer(customerId: CustomerId): List<Reminder> {
        return reminderRepository.findByContact(customerId)
    }
}