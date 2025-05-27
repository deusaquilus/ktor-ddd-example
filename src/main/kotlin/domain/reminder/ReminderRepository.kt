package domain.reminder


import com.example.domain.customer.CustomerId
import io.exoquery.capture
import io.exoquery.controller.jdbc.JdbcController
import io.exoquery.runOn

// Repository interface for Reminders.
interface ReminderRepository {
    suspend fun findById(id: ReminderId): Reminder?
    suspend fun save(reminder: Reminder): Reminder
    suspend fun findByContact(customerId: CustomerId): List<Reminder>
}

// Simple in-memory implementation of the ReminderRepository.
class InMemoryReminderRepository : ReminderRepository {
    private val reminders = mutableMapOf<ReminderId, Reminder>()
    private val sequence = java.util.concurrent.atomic.AtomicInteger(0)

    override suspend fun findById(id: ReminderId): Reminder? = reminders[id]

    override suspend fun save(reminder: Reminder): Reminder {
        val id = reminder.id ?: ReminderId(sequence.incrementAndGet().toLong())
        val reminderWithId = reminder.copy(id = id)
        reminders[id] = reminderWithId
        return reminderWithId
    }

    override suspend fun findByContact(customerId: CustomerId): List<Reminder> =
        reminders.values.filter { it.customerId == customerId }
}

// Jdbc implementation of the ReminderRepository.
// It is simple enough that does not need a DAO pattern or Row-Spcific objects.
class JdbcReminderRepository(private val controller: JdbcController) : ReminderRepository {
    override suspend fun findById(id: ReminderId): Reminder? =
        capture {
            Table<Reminder>().filter { r -> r.id == param(id) }
        }.buildPrettyFor.Postgres().runOn(controller).firstOrNull()

    override suspend fun save(reminder: Reminder): Reminder =
        if (reminder.id == null) {
            insertReminder(reminder)
        } else {
            upsertReminder(reminder)
        }.runOn(controller)

    override suspend fun findByContact(customerId: CustomerId): List<Reminder> =
        capture.select {
            val r = from(Table<Reminder>())
            where { r.customerId == paramCustom(customerId, CustomerId.serializer()) }
            r
        }.buildPrettyFor.Postgres().runOn(controller)

    private fun insertReminder(r: Reminder) =
        capture {
            insert<Reminder> { setParams(r).excluding(id) }.returning { it }
        }.buildPrettyFor.Postgres()

    private fun upsertReminder(r: Reminder) =
        capture {
            insert<Reminder> {
                setParams(r).onConflictUpdate(id) { excluded ->
                    set(remindAt to excluded.remindAt, message to excluded.message)
                }
            }.returning { it }
        }.buildPrettyFor.Postgres()
}
