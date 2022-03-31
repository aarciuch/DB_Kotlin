package art.tm.db_kotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings

@Entity(tableName = "person_table")
data class Person (
    @PrimaryKey(autoGenerate = true)
     var id: Long = 0,

    @ColumnInfo(name = "name_column")
    var name: String,

    @ColumnInfo(name = "age_column")
    var age: Int )
{

}
