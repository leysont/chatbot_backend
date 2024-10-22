package models_dialog

import kotlinx.serialization.Serializable

/**
 * Represents an employee in the system with an ID, first name, last name, and optional specialties.
 *
 * @property id The unique identifier for the employee.
 * @property firstName The first name of the employee.
 * @property lastName The last name of the employee.
 * @property specialties An optional string describing the specialties of the employee.
 */
@Serializable
data class Employee(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val specialties: String? = null
)