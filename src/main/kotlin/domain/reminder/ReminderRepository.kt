package domain.reminder


import com.example.domain.customer.CustomerId

// Repository interface for Reminders.
interface ReminderRepository {
    fun findById(id: ReminderId): Reminder?
    fun save(reminder: Reminder)
    fun findByContact(customerId: CustomerId): List<Reminder>
}

// Simple in-memory implementation of the ReminderRepository.
class InMemoryReminderRepository : ReminderRepository {
    private val reminders = mutableMapOf<ReminderId, Reminder>()
    private val sequence = java.util.concurrent.atomic.AtomicInteger(0)

    override fun findById(id: ReminderId): Reminder? = reminders[id]

    override fun save(reminder: Reminder) {
        val id = reminder.id ?: ReminderId(sequence.incrementAndGet().toLong())
        reminders[id] = reminder.copy(id = id)
    }

    override fun findByContact(customerId: CustomerId): List<Reminder> =
        reminders.values.filter { it.customerId == customerId }

}
