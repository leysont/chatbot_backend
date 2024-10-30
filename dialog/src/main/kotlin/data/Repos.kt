package data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import models.Customer
import models.Employee
import models.Server
import models.Ticket

/**
 * Singleton object that hosts various repository objects for managing
 * Customers, Tickets, and Employees.
 */
object Repos {

    val client = HttpClient(CIO) {
        defaultRequest {
            url("https://storage/")
        }
    }

    suspend inline fun <reified T> HttpClient.getOrNull(url: String): T? =
        get(url).let {
            if (it.status.isSuccess())
                it.body()
            else null
        }

    suspend inline fun <reified T> HttpClient.postAndReturn(url: String, body: T): T =
        post(url) {
            setBody(body)
        }.body()

    suspend inline fun HttpClient.deleteReturnBoolean(url: String): Boolean =
        delete(url).status.isSuccess()

    /**
     * Repository for managing Customers.
     */
    object Customers : Repository<Customer> {
        const val PATH = "customers"
        override suspend fun getAll(): List<Customer>? =
            client.getOrNull(PATH)

        override suspend fun get(id: Int): Customer? =
            client.getOrNull("$PATH/$id")

        override suspend fun add(entity: Customer): Customer =
            client.postAndReturn(PATH, entity)

        override suspend fun delete(id: Int): Boolean =
            client.deleteReturnBoolean("$PATH/$id")
    }

    /**
     * Repository for managing Customers.
     */
    object Tickets : Repository<Ticket> {
        const val PATH = "tickets"
        override suspend fun getAll(): List<Ticket>? =
            client.getOrNull(PATH)

        override suspend fun get(id: Int): Ticket? =
            client.getOrNull("$PATH/$id")

        override suspend fun add(entity: Ticket): Ticket =
            client.postAndReturn(PATH, entity)

        override suspend fun delete(id: Int): Boolean =
            client.deleteReturnBoolean("$PATH/$id")
    }


    /**
     * Repository for managing Customers.
     */
    object Employees : Repository<Employee> {
        const val PATH = "employees"
        override suspend fun getAll(): List<Employee>? =
            client.getOrNull(PATH)

        override suspend fun get(id: Int): Employee? =
            client.getOrNull("$PATH/$id")

        override suspend fun add(entity: Employee): Employee =
            client.postAndReturn(PATH, entity)

        override suspend fun delete(id: Int): Boolean =
            client.deleteReturnBoolean("$PATH/$id")
    }

    /**
     * Repository for managing Servers.
     */
    object Servers : Repository<Server> {
        const val PATH = "servers"
        override suspend fun getAll(): List<Server>? =
            client.getOrNull(PATH)

        override suspend fun get(id: Int): Server? =
            client.getOrNull("$PATH/$id")

        override suspend fun add(entity: Server): Server =
            client.postAndReturn(PATH, entity)

        override suspend fun delete(id: Int): Boolean =
            client.deleteReturnBoolean("$PATH/$id")
    }
}