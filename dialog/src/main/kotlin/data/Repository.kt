package data

import models_dialog.Customer

/**
 * A generic repository interface for managing a collection of objects.
 *
 * @param T the type of objects this repository manages.
 */
interface Repository<T> {

    /**
     * Adds an object to the repository.
     *
     * @param entity the object to add to the repository.
     * @return the added object.
     */
    suspend fun add(entity: T): T

    /**
     * Retrieves all objects from the repository.
     *
     * @return a list containing all objects managed by the repository.
     */
    suspend fun getAll(): List<T>?

    /**
     * Retrieves an object by its unique identifier.
     *
     * @param id the unique identifier of the object to retrieve.
     * @return the object associated with the given identifier, or null if no such object exists.
     */
    suspend fun get(id: Int): T?

    /**
     * Deletes an object from the repository by its unique identifier.
     *
     * @param id the unique identifier of the object to delete.
     * @return true if the object was successfully deleted, false otherwise.
     */
    suspend fun delete(id: Int): Boolean
}