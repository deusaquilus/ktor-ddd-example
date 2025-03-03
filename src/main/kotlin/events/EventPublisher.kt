package events

import com.example.events.DomainEvent

interface EventPublisher {
    fun publish(event: DomainEvent)
}

class EventPublisherImpl : EventPublisher {
    override fun publish(event: DomainEvent) {
        TODO("Not yet implemented")
    }
}

