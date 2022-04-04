package it.sapienza.macc.sharet.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ReservationDatabaseDao {

    @Insert
    suspend fun insert(reservation: Reservation)
}