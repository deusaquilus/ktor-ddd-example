package events

import com.example.events.DomainEvent

interface EventPublisher {
    fun publish(event: DomainEvent)
}

class EventPublisherImpl : EventPublisher {
    override fun publish(event: DomainEvent) {
        // For demo purposes, just print the event to console
        println("Event published: $event")
    }
}
