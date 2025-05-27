-- data class CustomerRow(
--         val id: CustomerId,
--         val name: String
--     )
create table if not exists customer (
    id serial primary key,
    name text not null
);


-- data class NoteRow(
--     val id: NoteId,
--     val customerId: CustomerId,
--     val content: String,
--     @Serializable(with = LocalDateTimeSerializer::class)
--     val createdAt: LocalDateTime
--   )
create table if not exists note (
    id serial primary key,
    customerId int not null,
    content text not null,
    createdAt text not null
);

-- data class ContactRow(
--     val id: ContactId,
--     val customerId: CustomerId,
--     val name: String,
--     val email: String,
--     val phone: String
--   )
create table if not exists contact (
    id serial primary key,
    customerId int not null,
    name text not null,
    email text not null,
    phone text not null
);

-- data class Reminder(
--     val id: ReminderId? = null,
--     val customerId: CustomerId, // always links to a customer
--     val noteId: NoteId?, // Optionally linked to a note
--     @Serializable(with = LocalDateTimeSerializer::class)
--     val remindAt: LocalDateTime,
--     val message: String
-- )
create table if not exists reminder (
    id serial primary key,
    customerId int not null,
    noteId int,
    remindAt text not null,
    message text not null
);
