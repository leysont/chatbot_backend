package data

import kotlinx.coroutines.Dispatchers
import models.Customer
import models.Employee
import models.Ticket
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

enum class ServiceLevel {
    Bronze,
    Silver,
    Gold,
}

object CustomerTable : IntIdTable() {
    val name = varchar("name", 100)

    //    val serviceLevel = integer("service_level").default(1)
    val serviceLevel = enumerationByName("service_level", 10, ServiceLevel::class)
    val notes = text("notes")
}

object EmployeeTable : IntIdTable() {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val specialties = text("specialties").nullable()
}

object TicketTable : IntIdTable() {
    val title = varchar("name", 100)
    val customerId = reference("customer_id", CustomerTable)
    val issueDescription = text("text")
    val employeeId = reference("employee_id", EmployeeTable)
    val express = bool("express").default(false)
    val serviceLevel = enumerationByName("service_level", 10, ServiceLevel::class)
}

class CustomerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CustomerDAO>(CustomerTable)

    var name by CustomerTable.name
    var serviceLevel by CustomerTable.serviceLevel
    var notes by CustomerTable.notes

    fun toModel() = Customer(
        id.value,
        name,
        serviceLevel,
        notes,
    )
}

class EmployeeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EmployeeDAO>(EmployeeTable)

    var firstName by EmployeeTable.firstName
    var lastName by EmployeeTable.lastName
    var specialties by EmployeeTable.specialties

    fun toModel() = Employee(
        id.value,
        firstName,
        lastName,
        specialties,
    )
}

class TicketDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TicketDAO>(TicketTable)

    var title by TicketTable.title
    var customer by CustomerDAO referencedOn TicketTable.customerId
    var issueDescription by TicketTable.issueDescription
    var employee by EmployeeDAO referencedOn TicketTable.employeeId
    var express by TicketTable.express
    var serviceLevel by TicketTable.serviceLevel

    fun toModel() = Ticket(
        id.value,
        title,
        customer.toModel(),
        issueDescription,
        employee.toModel(),
        express,
    )
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)