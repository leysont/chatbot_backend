package models_dialog

import kotlinx.serialization.Serializable
import models_dialog.ServiceLevel.Bronze

/**
 * Represents a customer in the system with an ID, name, service level, and notes.
 *
 * @property id The unique identifier for the customer.
 * @property name The name of the customer.
 * @property serviceLevel The service level of the customer, which defaults to Bronze.
 * @property notes Any additional notes about the customer.
 */
@Serializable
data class Customer (
    val id: Int? = null,
    val name: String,
    val serviceLevel: ServiceLevel = Bronze,
    val notes: String = "",
)