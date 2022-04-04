package it.sapienza.macc.sharet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation_table")
data class Reservation(

        @PrimaryKey(autoGenerate = true)
        var resourceId: Long = 0L,

        @ColumnInfo(name = "reservation_name")
        var reservationName: String = "not_initialized"

)