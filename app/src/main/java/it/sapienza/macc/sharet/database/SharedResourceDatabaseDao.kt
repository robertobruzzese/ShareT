package it.sapienza.macc.sharet.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SharedResourceDatabaseDao {

    @Insert
    suspend fun insert(sharedResource: SharedResource)


    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param sharedResource new value to write
     */
    @Update
    suspend fun update(sharedResource: SharedResource)

    /**
     * Selects and returns the row that matches the id of the resource, which is our key.
     *
     * @param key resource id to match
     */
    @Query("SELECT * from shared_resource_table WHERE resourceId = :key")
    suspend fun get(key: Long): SharedResource?

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM shared_resource_table")
    suspend fun clear()


    /**
     * Selects and returns all rows in the table
     */
    @Query("SELECT * FROM shared_resource_table ORDER BY resourceId DESC")
    fun getAllResources(): LiveData<List<SharedResource>>

    /**
     * Selects and returns the latest resource.
     */
    @Query("SELECT * FROM shared_resource_table ORDER BY resourceId DESC LIMIT 1")
    suspend fun getResource(): SharedResource?

    /**
     * Selects and returns the resource with given name.
     */
    @Query("SELECT * from shared_resource_table WHERE resourceId = :key")
    fun getResourceWithId(key: Long): LiveData<SharedResource>
}