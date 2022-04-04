package it.sapienza.macc.sharet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_resource_table")
data class SharedResource(

    @PrimaryKey(autoGenerate = true)
    var resourceId: Long = 0L,

    @ColumnInfo(name = "resource_name")
    var resourceName: String = "not_initialized"
)